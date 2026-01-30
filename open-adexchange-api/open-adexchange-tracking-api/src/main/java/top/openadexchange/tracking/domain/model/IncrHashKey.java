package top.openadexchange.tracking.domain.model;

import lombok.Data;

@Data
public class IncrHashKey {

    private String key;
    private String field;

    public IncrHashKey(String key, String field) {
        this.key = key;
        this.field = field;
    }
}
