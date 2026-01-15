package top.openadexchange.mos.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.DspDto;
import top.openadexchange.dto.DspPlacementMappingDto;
import top.openadexchange.dto.DspPlacementMappingQuery;
import top.openadexchange.dto.DspSecretDto;
import top.openadexchange.dto.DspSettingDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.DspQueryDto;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspPlacementMapping;
import top.openadexchange.mos.application.service.DspService;

import java.util.List;

@RestController
@RequestMapping("/v1/dsps")
@Tag(name = "DSP管理",
        description = "DSP管理，包括DSP的增删改查")
public class DspController {

    @Resource
    private DspService dspService;

    @PostMapping
    @Operation(summary = "新增DSP, 创建成功返回DSP ID")
    public ApiResponse<Integer> addDsp(@RequestBody DspDto dspDto) {
        return ApiResponse.success(dspService.addDsp(dspDto));
    }

    @PutMapping
    @Operation(summary = "更新DSP")
    public ApiResponse<Boolean> updateDsp(@RequestBody DspDto dspDto) {
        return ApiResponse.success(dspService.updateDsp(dspDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除DSP")
    public ApiResponse<Boolean> deleteDsp(@PathVariable("id") Integer id) {
        return ApiResponse.success(dspService.deleteDsp(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取DSP")
    public ApiResponse<DspDto> getDsp(@PathVariable("id") Integer id) {
        return ApiResponse.success(dspService.getDsp(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用DSP")
    public ApiResponse<Boolean> enableDsp(@PathVariable("id") Integer id) {
        return ApiResponse.success(dspService.enableDsp(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用DSP")
    public ApiResponse<Boolean> disableDsp(@PathVariable("id") Integer id) {
        return ApiResponse.success(dspService.disableDsp(id));
    }

    @GetMapping
    @Operation(summary = "分页查询DSP")
    public ApiResponse<Page<Dsp>> pageListDsp(DspQueryDto queryDto) {
        return ApiResponse.success(dspService.pageListDsp(queryDto));
    }

    @PutMapping("/{id}/setting")
    @Operation(summary = "DSP设置",
            description = "DSP设置，包括广告位、QPS限制、定向")
    public ApiResponse<Boolean> dspSetting(@PathVariable("id") Integer dspId,
            @RequestBody DspSettingDto dspSettingDto) {
        return ApiResponse.success(dspService.dspSetting(dspId, dspSettingDto));
    }


    @GetMapping("/{id}/setting")
    @Operation(summary = "获取DSP设置")
    public ApiResponse<DspSettingDto> getDspSetting(@PathVariable("id") Integer dspId) {
        return ApiResponse.success(dspService.getDspSetting(dspId));
    }

    @GetMapping("/{id}/secret")
    @Operation(summary = "查看DSP密钥信息",
            description = "获取DSP密钥相关信息，包括API令牌、加密密钥、完整性密钥")
    public ApiResponse<DspSecretDto> getDspSecret(@PathVariable("id") Integer dspId) {
        return ApiResponse.success(dspService.getDspSecret(dspId));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索DSP, 按DSP名称模糊搜索")
    public ApiResponse<List<Dsp>> search(@RequestParam(name = "searchKey",
            required = false) String searchKey) {
        return ApiResponse.success(dspService.search(searchKey));
    }

    @GetMapping("/placement-mappings")
    @Operation(summary = "分页查询DSP广告位映射")
    public ApiResponse<Page<DspPlacementMappingDto>> pageDspPlacementMappings(DspPlacementMappingQuery query) {
        return ApiResponse.success(dspService.pageDspPlacementMappings(query));
    }

    @PostMapping("/placement-mappings")
    @Operation(summary = "新增DSP广告位映射")
    public ApiResponse<Integer> addDspPlacementMapping(@RequestBody DspPlacementMapping dspPlacementMapping) {
        return ApiResponse.success(dspService.addDspPlacementMapping(dspPlacementMapping));
    }

    @DeleteMapping("/placement-mappings/{id}")
    @Operation(summary = "删除DSP广告位映射")
    public ApiResponse<Boolean> deleteDspPlacementMapping(@PathVariable("id") Integer id) {
        return ApiResponse.success(dspService.deleteDspPlacementMapping(id));
    }
}