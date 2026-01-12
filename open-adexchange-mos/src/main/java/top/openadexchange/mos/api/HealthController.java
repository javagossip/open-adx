package top.openadexchange.mos.api;

import com.ruoyi.common.annotation.Anonymous;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.openadexchange.dto.commons.ApiResponse;

@RestController
@RequestMapping("/health")
@Anonymous
public class HealthController {

    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }
}
