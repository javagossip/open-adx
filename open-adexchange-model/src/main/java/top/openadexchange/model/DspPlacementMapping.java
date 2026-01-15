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
 * @since 2026-01-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dsp_placement_mapping")
public class DspPlacementMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer dspId;

    private Integer siteAdPlacementId;

    /**
     * dsp 平台广告位编码/id
     */
    private String dspSlotId;

}
