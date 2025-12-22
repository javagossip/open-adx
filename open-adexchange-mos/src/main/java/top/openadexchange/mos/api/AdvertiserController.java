package top.openadexchange.mos.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.AdvertiserAggregateDto;
import top.openadexchange.dto.AdvertiserDto;
import top.openadexchange.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.AdvertiserQueryDto;
import top.openadexchange.model.Advertiser;
import top.openadexchange.mos.application.service.AdvertiserIndustryLicenseService;
import top.openadexchange.mos.application.service.AdvertiserService;

@RestController
@RequestMapping("/v1/advertisers")
@Tag(name = "广告主管理",
        description = "广告主管理，包括广告主的增删改查")
public class AdvertiserController {

    @Resource
    private AdvertiserService advertiserService;

    @Resource
    private AdvertiserIndustryLicenseService advertiserIndustryLicenseService;

    @PostMapping
    @Operation(summary = "新增广告主, 创建成功返回广告主ID")
    public ApiResponse<Long> addAdvertiser(@RequestBody AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.addAdvertiser(advertiserDto));
    }

    @PutMapping
    @Operation(summary = "更新广告主")
    public ApiResponse<Boolean> updateAdvertiser(@RequestBody AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.updateAdvertiser(advertiserDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告主")
    public ApiResponse<Boolean> deleteAdvertiser(@PathVariable("id") Long id) {
        return ApiResponse.success(advertiserService.deleteAdvertiser(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取广告主")
    public ApiResponse<AdvertiserDto> getAdvertiser(@PathVariable("id") Long id) {
        return ApiResponse.success(advertiserService.getAdvertiser(id));
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "获取广告主聚合详情，包括广告主基本信息以及资质和审核信息")
    public ApiResponse<AdvertiserAggregateDto> getAdvertiserDetail(@PathVariable("id") Long id) {
        return ApiResponse.success(advertiserService.getAdvertiserAggregateDetail(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用广告主")
    public ApiResponse<Boolean> enableAdvertiser(@PathVariable("id") Long id) {
        return ApiResponse.success(advertiserService.enableAdvertiser(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用广告主")
    public ApiResponse<Boolean> disableAdvertiser(@PathVariable("id") Long id) {
        return ApiResponse.success(advertiserService.disableAdvertiser(id));
    }

    @GetMapping
    @Operation(summary = "分页查询广告主")
    public ApiResponse<Page<Advertiser>> pageListAdvertisers(AdvertiserQueryDto queryDto) {
        return ApiResponse.success(advertiserService.pageListAdvertisers(queryDto));
    }

    // 广告主行业资质管理接口

    @PostMapping("/{advertiserId}/licenses")
    @Operation(summary = "为广告主添加行业资质")
    public ApiResponse<Long> addLicense(@PathVariable("advertiserId") Long advertiserId,
            @RequestBody AdvertiserIndustryLicenseDto licenseDto) {
        licenseDto.setAdvertiserId(advertiserId);
        return ApiResponse.success(advertiserIndustryLicenseService.addLicense(licenseDto));
    }

    @PutMapping("/licenses/{licenseId}")
    @Operation(summary = "更新广告主行业资质")
    public ApiResponse<Boolean> updateLicense(@PathVariable("licenseId") Long licenseId,
            @RequestBody AdvertiserIndustryLicenseDto licenseDto) {
        licenseDto.setId(licenseId);
        return ApiResponse.success(advertiserIndustryLicenseService.updateLicense(licenseDto));
    }

    @DeleteMapping("/licenses/{licenseId}")
    @Operation(summary = "删除广告主行业资质")
    public ApiResponse<Boolean> deleteLicense(@PathVariable("licenseId") Long licenseId) {
        return ApiResponse.success(advertiserIndustryLicenseService.deleteLicense(licenseId));
    }

    @GetMapping("/licenses/{licenseId}")
    @Operation(summary = "获取广告主行业资质详情")
    public ApiResponse<AdvertiserIndustryLicenseDto> getLicense(@PathVariable("licenseId") Long licenseId) {
        return ApiResponse.success(advertiserIndustryLicenseService.getLicense(licenseId));
    }

    @GetMapping("/{advertiserId}/licenses")
    @Operation(summary = "获取广告主的所有行业资质")
    public ApiResponse<List<AdvertiserIndustryLicenseDto>> getLicensesByAdvertiserId(@PathVariable("advertiserId") Long advertiserId) {
        return ApiResponse.success(advertiserIndustryLicenseService.getLicensesByAdvertiserId(advertiserId));
    }
}