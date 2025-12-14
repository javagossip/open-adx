package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table("publisher")
public class Publisher implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;
    //1-'individual', 2-'company'
    private Integer type;

    private String contactEmail;

    private String contactPhone;

    private Long userId;

    /**
     * 1=active,0=inactive
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
