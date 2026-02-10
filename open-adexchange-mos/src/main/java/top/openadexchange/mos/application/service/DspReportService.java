package top.openadexchange.mos.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dto.query.DspReportQueryDto;
import top.openadexchange.dto.report.DspReportDto;

import static com.mybatisflex.core.query.QueryMethods.*;
import static top.openadexchange.model.table.DspStatTableDef.*;
import static top.openadexchange.model.table.DspTableDef.*;

@Service
@Slf4j
public class DspReportService {

    @Resource
    private DspStatDao dspStatDao;
    @Resource
    private RedisAdStatService redisAdStatService;

    public Page<DspReportDto> pageDspReports(DspReportQueryDto queryDto) {
        // 获取当前小时的时间戳(yyyyMMddHH格式)
        Integer currentHour = Integer.parseInt(LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT));
        
        // 检查查询时间范围是否包含当前小时
        boolean needMergeCurrentHourData = queryDto.getStartDate() <= currentHour && queryDto.getEndDate() >= currentHour;
        
        // 构建查询条件 - 如果需要合并当前小时数据，则排除当前小时的数据
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(DSP.DSP_ID.as("dsp_code"),
                        DSP.NAME.as("dsp_name"),
                        sum(DSP_STAT.IMP_COUNT).as("imp_count"),
                        sum(DSP_STAT.CLK_COUNT).as("clk_count"),
                        sum(DSP_STAT.BID_COUNT).as("bid_count"),
                        sum(DSP_STAT.WIN_COUNT).as("win_count"),
                        sum(DSP_STAT.REQ_COUNT).as("req_count"),
                        DSP_STAT.STAT_DATE,
                        sum(DSP_STAT.COST).as("cost"))
                .from(DSP)
                .leftJoin(DSP_STAT)
                .on(DSP_STAT.DSP_CODE.eq(DSP.DSP_ID)
                        .and(needMergeCurrentHourData ? DSP_STAT.STAT_DATE.ne(currentHour) : null)
                        .and(DSP_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())));
        
        // 添加DSP查询条件
        queryWrapper.where(DSP.NAME.like(queryDto.getDspName()).and(DSP.DSP_ID.eq(queryDto.getDspCode())));
        
        queryWrapper.groupBy(DSP.DSP_ID, DSP.NAME, DSP_STAT.STAT_DATE)
                .orderBy(DSP_STAT.IMP_COUNT.desc());

        Page<DspReportDto> dspReports = dspStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                queryWrapper,
                DspReportDto.class);
        
        // 如果不需要合并当前小时数据或没有记录，直接返回
        if (!needMergeCurrentHourData || !dspReports.hasRecords()) {
            log.info("查询DSP报表, 时间范围不包含当前小时或无数据, 开始日期：{}, 结束日期：{}, 当前小时：{}", 
                    queryDto.getStartDate(), queryDto.getEndDate(), currentHour);
            return dspReports;
        }
        
        // 获取所有DSP编码（LEFT JOIN已包含所有符合条件的DSP）
        List<String> dspCodes = dspReports.getRecords().stream()
                .filter(Objects::nonNull)
                .map(DspReportDto::getDspCode)
                .filter(code -> code != null && !code.trim().isEmpty())
                .distinct()
                .toList();
        
        if (dspCodes.isEmpty()) {
            log.info("没有有效的DSP编码用于查询缓存数据");
            return dspReports;
        }
        
        log.info("查询DSP统计当前一小时的缓存数据, DSP编码: {}", dspCodes);
        Map<String, DspReportDto> currentHourCacheMap = redisAdStatService.getTodayDspStatsAggregateDspCodes(dspCodes);
        log.info("查询DSP当天统计报表, 缓存数据获取完成, 缓存记录数: {}", currentHourCacheMap != null ? currentHourCacheMap.size() : 0);
        
        if (currentHourCacheMap == null || currentHourCacheMap.isEmpty()) {
            log.info("最近一小时DSP统计数据为空");
            return dspReports;
        }
        
        // 直接将缓存数据添加到报表列表中
        currentHourCacheMap.values().forEach(cacheDto -> {
            DspReportDto newRecord = createNewRecordFromCache(cacheDto, currentHour);
            dspReports.getRecords().add(newRecord);
        });
        
        return dspReports;
    }


    
    /**
     * 从缓存数据创建新的报表记录
     */
    private DspReportDto createNewRecordFromCache(DspReportDto cacheDto, Integer currentHour) {
        DspReportDto newRecord = new DspReportDto();
        newRecord.setDspCode(cacheDto.getDspCode());
        newRecord.setDspName(cacheDto.getDspName());
        newRecord.setStatDate(currentHour);
        
        // 设置统计数据
        newRecord.setReqCount(cacheDto.getReqCount());
        newRecord.setBidCount(cacheDto.getBidCount());
        newRecord.setWinCount(cacheDto.getWinCount());
        newRecord.setImpCount(cacheDto.getImpCount());
        newRecord.setClkCount(cacheDto.getClkCount());
        newRecord.setCost(cacheDto.getCost());
        
        return newRecord;
    }
}
