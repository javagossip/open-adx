package top.openadexchange.tracking.api;

import jakarta.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.openadexchange.tracking.application.service.TrackingService;

@RestController
@RequestMapping("/v1/tracking")
public class TrackingController {

    @Resource
    private TrackingService trackingService;

    @GetMapping("/imp")
    public ResponseEntity<Boolean> impTracking() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/click")
    public ResponseEntity<Boolean> clickTracking() {
        return ResponseEntity.ok(true);
    }
}
