package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DSP密钥信息,包括 API-token, 价格加解密密钥")
public class DspSecretDto {

    @Schema(description = "api token")
    private String apiToken;
    @Schema(description = "加密密钥")
    private String encryptionKey;
    @Schema(description = "完整性密钥")
    private String integrityKey;
}
