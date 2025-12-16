package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "字典选项")
public class OptionDto {
    @Schema(description = "选项标签")
    private String label;
    @Schema(description = "选项值")
    private String value;
}
