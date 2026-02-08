package top.openadexchange.domain.entity;

import java.util.List;

import lombok.Data;
import top.openadexchange.model.Creative;
import top.openadexchange.model.CreativeAsset;

@Data
public class CreativeAggregate {

    private Creative creative;
    private List<CreativeAsset> creativeAssets;
    
}
