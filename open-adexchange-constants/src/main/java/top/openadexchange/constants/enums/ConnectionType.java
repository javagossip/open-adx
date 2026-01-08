package top.openadexchange.constants.enums;

import lombok.Getter;

@Getter
public enum ConnectionType {
    UNKNOWN(0),
    WIFI(1),
    MOBILE_2G(2),
    MOBILE_3G(3),
    MOBILE_4G(4),
    MOBILE_5G(5);

    private final int value;

    ConnectionType(int value) {
        this.value = value;
    }

    public static ConnectionType fromValue(int value) {
        for (ConnectionType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
