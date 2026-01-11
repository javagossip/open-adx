package top.openadexchange.domain.entity;

import lombok.Data;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.NativeAsset;

import java.util.List;

@Data
public class AdPlacementAggregate {

    private AdPlacement adPlacement;
    private List<NativeAsset> nativeAssets;
}
