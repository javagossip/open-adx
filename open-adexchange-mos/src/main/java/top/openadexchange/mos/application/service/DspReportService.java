package top.openadexchange.mos.application.service;

import com.mybatisflex.core.paginate.Page;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dto.query.DspReportQueryDto;
import top.openadexchange.dto.report.DspReportDto;
import top.openadexchange.model.DspStat;

import static top.openadexchange.model.table.DspStatTableDef.*;

@Service
public class DspReportService {

    @Resource
    private DspStatDao dspStatDao;

    public Page<DspReportDto> pageDspReports(DspReportQueryDto queryDto) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(DSP_STAT.DSP_ID,
                        DSP_STAT.DSP_CODE,
                        DSP_STAT.DSP_NAME,
                        DSP_STAT.IMP_COUNT,
                        DSP_STAT.CLK_COUNT,
                        DSP_STAT.BID_COUNT,
                        DSP_STAT.WIN_COUNT,
                        DSP_STAT.STAT_DATE)
                .from(DSP_STAT)
                .eq(DspStat::getDspCode, queryDto.getDspCode())
                .like(DspStat::getDspName, queryDto.getDspName())
                .between(DspStat::getStatDate, queryDto.getStartDate(), queryDto.getEndDate());

        return dspStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                queryWrapper,
                DspReportDto.class);
    }
}
