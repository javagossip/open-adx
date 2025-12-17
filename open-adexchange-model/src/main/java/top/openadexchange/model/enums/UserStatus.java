package top.openadexchange.model.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("0", "正常"),
    DISABLED("1", "禁用");

    private final String value;
    private final String description;

    UserStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
