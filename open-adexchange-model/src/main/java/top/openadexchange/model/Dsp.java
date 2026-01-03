package top.openadexchange.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类。
 *
 * @author weiping
 * @since 2025-12-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dsp")
public class Dsp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String dspId;
    private Long userId;
    private String name;

    private String bidEndpoint;
    private String winNoticeEndpoint;
    //dsp API token
    private String token;
    //dsp价格加解密密钥配置
    private String encryptionKey;
    private String integrationKey;

    private String contactName;
    private String contactPhone;
    private String contactEmail;
    /**
     * 1=active,0=inactive
     */
    private Integer status;

    private Integer qpsLimit;

    private Integer timeoutMs;
    private String brandLogo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
