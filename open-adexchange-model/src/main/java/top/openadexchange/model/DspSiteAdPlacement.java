package top.openadexchange.model;

import java.io.Serial;
import java.io.Serializable;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dsp和媒体广告位的绑定关系(dsp需要哪些广告位流量) 实体类。
 *
 * @author weiping
 * @since 2025-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dsp_site_ad_placement")
public class DspSiteAdPlacement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * dsp平台ID
     */
    @Id
    private Integer dspId;

    /**
     * 媒体广告位ID
     */
    @Id
    private Integer siteAdPlacementId;

}
