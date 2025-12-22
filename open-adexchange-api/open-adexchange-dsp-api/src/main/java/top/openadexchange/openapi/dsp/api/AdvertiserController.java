package top.openadexchange.openapi.dsp.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import top.openadexchange.commons.dto.ApiResponse;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.service.AdvertiserService;

@RestController
@RequestMapping("/v1/advertisers")
public class AdvertiserController {

    @Resource
    private AdvertiserService advertiserService;

    @PostMapping
    public ApiResponse<String> addAdvertiser(AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.addAdvertiser(advertiserDto));
    }

    public ApiResponse<Boolean> updateAdvertiser(AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.updateAdvertiser(advertiserDto));
    }
}
