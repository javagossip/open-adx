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
    private Integer id;
    @Schema(description = "DSP名称")
    private String name;
    @Schema(description = "DSP编码")
    private String code;
    @Schema(description = "DSP登录密码")
    private String password;
    @Schema(description = "Bid端点")
    private String bidEndpoint;
    @Schema(description = "Win通知端点")
    private String winNoticeEndpoint;
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
    @Schema(description = "DSP RTB协议类型：1-标准 2- dsp平台自有协议")
    private int rtbProtocolType;
    @Schema(description = "加密密钥")
    private String encryptionKey;
    @Schema(description = "完整性密钥")
    private String integrityKey;
}
