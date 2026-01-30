package top.openadexchange.constants.enums;

import lombok.Getter;

@Getter
public enum AuctionType {
    FIRST_PRICE(1),
    SECOND_PRICE(2);

    private final int value;

    AuctionType(int value) {
        this.value = value;
    }

    public static AuctionType fromValue(int value) {
        for (AuctionType type : AuctionType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
