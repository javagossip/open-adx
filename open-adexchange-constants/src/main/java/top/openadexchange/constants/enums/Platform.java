package top.openadexchange.constants.enums;

import lombok.Getter;

@Getter
public enum Platform {
    ANDROID("Android"),
    IOS("iOS"),
    WEB("Web");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

    public static Platform fromValue(String value) {
        for (Platform platform : values()) {
            if (platform.value.equalsIgnoreCase(value)) {
                return platform;
            }
        }
        return null;
    }
}
