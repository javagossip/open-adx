package top.openadexchange.tracking.api;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.tracking.application.service.TrackingService;
import top.openadexchange.tracking.domain.gateway.OaxTrackingServices;

@RestController
@RequestMapping("/v1/track")
@Slf4j
public class TrackingController {

    @Resource
    private TrackingService trackingService;
    @Resource
    private OaxTrackingServices oaxTrackingServices;

    @GetMapping("/imp")
    @Operation(summary = "广告曝光")
    public ResponseEntity<Void> impTracking(@RequestParam("tk") String tk, HttpServletRequest request) {
        try {
            ExecutorService executor = oaxTrackingServices.getExecutor();
            Assert.notNull(executor, "executor is null");
            CompletableFuture.runAsync(() -> trackingService.impTrack(tk, request), executor)
                    .exceptionally(throwable -> {
                        log.error("impTrack error", throwable);
                        return null;
                    });
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/click")
    @Operation(summary = "广告点击")
    public ResponseEntity<Void> clickTracking(@RequestParam("tk") String tk, HttpServletRequest request) {
        try {
            ExecutorService executor = oaxTrackingServices.getExecutor();
            Assert.notNull(executor, "executor is null");
            CompletableFuture.runAsync(() -> trackingService.clkTrack(tk, request), executor)
                    .exceptionally(throwable -> {
                        log.error("clkTrack error", throwable);
                        return null;
                    });
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.noContent().build();
        }
    }
}
