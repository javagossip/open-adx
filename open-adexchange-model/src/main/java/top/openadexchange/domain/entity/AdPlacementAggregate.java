package top.openadexchange.domain.entity;

import java.util.List;

import lombok.Data;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.NativeAsset;

@Data
public class AdPlacementAggregate {

    private AdPlacement adPlacement;
    private List<NativeAsset> nativeAssets;
}
