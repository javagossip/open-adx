package top.openadexchange.constants.enums;

import lombok.Getter;

@Getter
public enum SiteType {
    WEBSITE(1),
    APP(2);

    private final int value;

    SiteType(int value) {
        this.value = value;
    }

    public static SiteType fromValue(int value) {
        for (SiteType siteType : SiteType.values()) {
            if (siteType.value == value) {
                return siteType;
            }
        }
        return null;
    }
}
