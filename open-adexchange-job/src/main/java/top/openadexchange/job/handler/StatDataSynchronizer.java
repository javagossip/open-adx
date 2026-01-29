package top.openadexchange.job.handler;

import com.alibaba.fastjson2.JSON;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import top.openadexchange.job.service.StatDataSynchronizerService;

@Component
@Slf4j
public class StatDataSynchronizer {

    @Resource
    private StatDataSynchronizerService statDataSynchronizerService;

    @XxlJob(value = "oax.stat.adslot.sync")
    public void syncAdSlotStatData() {
        try {
            String jobParam = XxlJobHelper.getJobParam();
            log.info("syncAdSlotStatData start, job param: {}", jobParam);
            StatDataSynchronizerParam param =
                    StringUtils.hasText(jobParam) ? JSON.parseObject(jobParam, StatDataSynchronizerParam.class) : null;
            statDataSynchronizerService.syncAdSlotStatData(param);
            log.info("syncAdSlotStatData end");
        } catch (Exception ex) {
            log.error("syncAdSlotStatData error", ex);
            throw ex;
        }
    }

    @XxlJob(value = "oax.stat.crid.sync")
    public void syncCridStatData() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("syncCridStatData start, job param: {}", jobParam);
        StatDataSynchronizerParam param =
                StringUtils.hasText(jobParam) ? JSON.parseObject(jobParam, StatDataSynchronizerParam.class) : null;
        // TODO: 实现具体的crid统计数据同步逻辑
        // 目前保留占位符，后续可以根据具体需求实现
        log.warn("syncCridStatData is not implemented yet");
    }

    @Data
    public static class StatDataSynchronizerParam {

        private String syncDate;
    }

}