package top.openadexchange.domain.entity;

import java.util.List;

import lombok.Data;

@Data
public class AdGroupAggregate {

    private List<CreativeAggregate> creatives;
}
