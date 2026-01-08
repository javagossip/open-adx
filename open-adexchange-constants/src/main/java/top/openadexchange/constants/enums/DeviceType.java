package top.openadexchange.constants.enums;

public enum DeviceType {
    UNKNOWN(0),
    PHONE(1),
    PAD(2),
    PC(3),
    TV(4);

    private final int value;

    DeviceType(int value) {
        this.value = value;
    }

    public static DeviceType fromValue(int value) {
        for (DeviceType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
