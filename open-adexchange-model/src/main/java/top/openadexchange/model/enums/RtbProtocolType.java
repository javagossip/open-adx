package top.openadexchange.model.enums;


import lombok.Getter;

@Getter
public enum RtbProtocolType {
    STANDARD(1),
    CUSTOM(2);

    private final int value;

    RtbProtocolType(int value) {
        this.value = value;
    }

    public static RtbProtocolType fromValue(int value) {
        for (RtbProtocolType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
