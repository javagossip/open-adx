package top.openadexchange.openapi.ssp.constants;

import lombok.Getter;

@Getter
public enum CacheType {
    PUBLISHER(1),
    SITE(2),
    SITE_AD_PLACEMENT(3),
    AD_PLACEMENT(4),
    DSP(5);

    private final int value;

    CacheType(int value) {
        this.value = value;
    }

    public static CacheType fromValue(int value) {
        for (CacheType type : CacheType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
