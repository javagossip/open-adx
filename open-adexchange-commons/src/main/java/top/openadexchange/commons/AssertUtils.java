package top.openadexchange.commons;

import java.util.Collection;

import io.micrometer.common.util.StringUtils;
import top.openadexchange.commons.exception.ErrorCode;

public final class AssertUtils {

    public static void assertTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw errorCode.toException();
        }
    }

    public static void notBlank(String str, ErrorCode errorCode) {
        if (StringUtils.isBlank(str)) {
            throw errorCode.toException();
        }
    }

    public static void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw errorCode.toException();
        }
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw errorCode.toException();
        }
    }
}
