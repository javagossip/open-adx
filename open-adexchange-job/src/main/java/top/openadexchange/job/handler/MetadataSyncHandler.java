package top.openadexchange.job.handler;

import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MetadataSyncHandler {

    @XxlJob(value = "oax.metadata.sync")
    public void metadataSyncHandler() {
        log.info("metadata sync job start");
    }
}
