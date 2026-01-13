package top.openadexchange.model.enums;

public enum RtbProtocolType {
    STANDARD(1),
    CUSTOM(2);

    private final int protocolType;

    RtbProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public static RtbProtocolType fromValue(int value) {
        for (RtbProtocolType type : values()) {
            if (type.protocolType == value) {
                return type;
            }
        }
        return null;
    }
}
