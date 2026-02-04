package top.openadexchange.mos.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dto.query.DspReportQueryDto;
import top.openadexchange.dto.report.DspReportDto;
import top.openadexchange.model.DspStat;

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
        Integer today = Integer.parseInt(LocalDate.now().format(Constants.REDIS_KEY_DATEFORMAT));
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(DSP.DSP_ID.as("dsp_code"),
                        DSP.NAME.as("dsp_name"),
                        DSP_STAT.IMP_COUNT,
                        DSP_STAT.CLK_COUNT,
                        DSP_STAT.BID_COUNT,
                        DSP_STAT.WIN_COUNT,
                        DSP_STAT.STAT_DATE,
                        DSP_STAT.COST)
                .from(DSP)
                .leftJoin(DSP_STAT)
                .on(DSP_STAT.DSP_CODE.eq(DSP.DSP_ID)
                        .and(DSP_STAT.STAT_DATE.ne(today))
                        .and(DSP_STAT.DSP_CODE.eq(queryDto.getDspCode()))
                        .and(DSP.NAME.like(queryDto.getDspName()))
                        .and(DSP_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())));

        Page<DspReportDto> dspReports = dspStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                queryWrapper,
                DspReportDto.class);
        if (!dspReports.hasRecords()) {
            return dspReports;
        }

        if (queryDto.getStartDate() > today || queryDto.getEndDate() < today) {
            log.info("查询DSP报表, 缓存数据开始日期：{}", queryDto.getStartDate());
            return dspReports;
        }
        List<String> dspCodes =
                dspReports.getRecords().stream().filter(Objects::nonNull).map(DspReportDto::getDspCode).toList();
        Map<String, DspReportDto> dspReportMap = redisAdStatService.getTodayAdSlotStatsAggregateDspCodes(dspCodes);
        log.info("查询DSP当天统计报表, 缓存数据结束, cache data: {}", dspReportMap);
        dspReports.getRecords().forEach(reportDto -> {
            DspReportDto dspReportDto = dspReportMap.get(reportDto.getDspCode());
            if (dspReportDto != null) {
                reportDto.incrImpCount(dspReportDto.getImpCount());
                reportDto.incrClkCount(dspReportDto.getClkCount());
                reportDto.incrCost(dspReportDto.getCost());
            }
        });
        return dspReports;
    }
}
