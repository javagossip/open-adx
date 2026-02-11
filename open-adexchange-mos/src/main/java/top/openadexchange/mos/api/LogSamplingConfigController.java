package top.openadexchange.mos.api;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.openadexchange.dto.LogSamplingConfigDto;
import top.openadexchange.dto.LogSamplingConfigQueryDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.mos.application.service.LogSamplingConfigService;

@RestController
@RequestMapping("/v1/log-sampling-configs")
@Tag(name = "日志采样配置管理", description = "日志采样配置的增删改查及分页查询")
public class LogSamplingConfigController {

    @Resource
    private LogSamplingConfigService logSamplingConfigService;

    @PostMapping
    @Operation(summary = "新增日志采样配置")
    public ApiResponse<Long> add(@RequestBody LogSamplingConfigDto dto) {
        return ApiResponse.success(logSamplingConfigService.add(dto));
    }

    @PutMapping
    @Operation(summary = "更新日志采样配置")
    public ApiResponse<Boolean> update(@RequestBody LogSamplingConfigDto dto) {
        return ApiResponse.success(logSamplingConfigService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除日志采样配置")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        return ApiResponse.success(logSamplingConfigService.delete(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取日志采样配置详情")
    public ApiResponse<LogSamplingConfigDto> get(@PathVariable("id") Long id) {
        return ApiResponse.success(logSamplingConfigService.getById(id));
    }

    @GetMapping
    @Operation(summary = "分页查询日志采样配置")
    public ApiResponse<Page<LogSamplingConfigDto>> page(LogSamplingConfigQueryDto queryDto) {
        return ApiResponse.success(logSamplingConfigService.page(queryDto));
    }
}

