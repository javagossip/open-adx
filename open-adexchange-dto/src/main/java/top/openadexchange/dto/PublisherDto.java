package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "媒体/出版商信息")
public class PublisherDto {

    @Schema(description = "媒体ID")
    private Long id;
    @Schema(description = "媒体名称")
    private String name;
    @Schema(description = "媒体登录密码")
    private String password;
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    @Schema(description = "联系人电话")
    private String contactPhone;

    /**
     * 1=active,0=inactive
     */
    @Schema(description = "状态, 1-使用中，0-停用")
    private Integer status;
}
