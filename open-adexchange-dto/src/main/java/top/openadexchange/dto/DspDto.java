package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DSP信息")
public class DspDto {

    @Schema(description = "DSP ID")
    private Long id;
    @Schema(description = "DSP名称")
    private String name;
    @Schema(description = "DSP登录密码")
    private String password;
    @Schema(description = "Bid端点")
    private String bidEndpoint;
    @Schema(description = "Win通知端点")
    private String winNoticeEndpoint;
//    @Schema(description = "DSP Token")
//    private String token;
    @Schema(description = "联系人名称")
    private String contactName;
    @Schema(description = "联系人电话")
    private String contactPhone;
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    /**
     * 1=active,0=inactive
     */
    @Schema(description = "状态, 1-使用中，0-停用")
    private Integer status;
    @Schema(description = "QPS限制")
    private Integer qpsLimit;
    @Schema(description = "超时时间(ms)")
    private Integer timeoutMs;
    @Schema(description = "品牌logo地址")
    private String brandLogo;
}
