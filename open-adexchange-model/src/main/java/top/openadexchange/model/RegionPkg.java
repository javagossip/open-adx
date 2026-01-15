package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地域包管理 实体类。
 *
 * @author mac
 * @since 2026-01-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("region_pkg")
public class RegionPkg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 地域包名称
     */
    private String name;

    /**
     * 1-地域分级专用，2 通用
     */
    private Integer type;

}
