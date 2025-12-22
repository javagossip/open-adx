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
 * @since 2025-12-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("domain_event")
public class DomainEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * event ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * event type
     */
    private String type;

    /**
     * 事件数据
     */
    private String payload;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * Pending,Closed,Failed
     */
    private String status;

}
