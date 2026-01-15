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
 *  实体类。
 *
 * @author mac
 * @since 2026-01-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("region_pkg_district")
public class RegionPkgDistrict implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer regionPkgId;

    /**
     * 区域编码
     */
    private String districtCode;

}
