package top.openadexchange.mos.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.PublisherDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.mos.application.service.PublisherService;

@RestController
@RequestMapping("/v1/publishers")
@Tag(name = "媒体/发布商管理",
        description = "媒体/发布商管理，包括媒体的增删改查")
public class PublisherController {

    @Resource
    private PublisherService publisherService;

    @PostMapping
    @Operation(summary = "新增媒体/发布商, 创建成功返回媒体/发布商ID")
    public ApiResponse<Long> addOrUpdatePublisher(@RequestBody PublisherDto publisherDto) {
        return ApiResponse.success(publisherService.addPublisher(publisherDto));
    }

    @PutMapping
    @Operation(summary = "更新媒体/发布商")
    public ApiResponse<Boolean> updatePublisher(@RequestBody PublisherDto publisherDto) {
        return ApiResponse.success(publisherService.updatePublisher(publisherDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除媒体/发布商")
    public ApiResponse<Boolean> deletePublisher(@PathVariable("id") Long id) {
        return ApiResponse.success(publisherService.deletePublisher(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取媒体/发布商")
    public ApiResponse<PublisherDto> getPublisher(@PathVariable("id") Long id) {
        return ApiResponse.success(publisherService.getPublisher(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用媒体/发布商")
    public ApiResponse<Boolean> enablePublisher(@PathVariable("id") Long id) {
        return ApiResponse.success(publisherService.enablePublisher(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用媒体/发布商")
    public ApiResponse<Boolean> disablePublisher(@PathVariable("id") Long id) {
        return ApiResponse.success(publisherService.disablePublisher(id));
    }
}
