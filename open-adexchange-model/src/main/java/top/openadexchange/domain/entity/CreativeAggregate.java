package top.openadexchange.domain.entity;

import lombok.Data;
import top.openadexchange.model.Creative;
import top.openadexchange.model.CreativeAsset;

import java.util.List;

@Data
public class CreativeAggregate {

    private Creative creative;
    private List<CreativeAsset> creativeAssets;
    
}
