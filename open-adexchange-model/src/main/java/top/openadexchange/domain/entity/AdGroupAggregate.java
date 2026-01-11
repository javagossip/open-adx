package top.openadexchange.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class AdGroupAggregate {

    private List<CreativeAggregate> creatives;
}
