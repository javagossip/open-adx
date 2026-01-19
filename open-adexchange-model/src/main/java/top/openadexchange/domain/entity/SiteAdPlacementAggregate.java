package top.openadexchange.domain.entity;

import lombok.Data;
import top.openadexchange.model.SiteAdPlacement;

import java.util.List;

@Data
public class SiteAdPlacementAggregate {

    private SiteAdPlacement siteAdPlacement;
    //媒体广告位关联系统广告位模板，可以关联多个广告位
    private List<Integer> adPlacementIds;

    public Integer getAdPlacementId() {
        return adPlacementIds != null && adPlacementIds.size() > 0 ? adPlacementIds.get(0) : null;
    }
}
