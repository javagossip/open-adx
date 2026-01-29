package top.openadexchange.tracking.application.factory;

import jakarta.servlet.http.HttpServletRequest;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.tracking.domain.event.ClickEvent;
import top.openadexchange.tracking.domain.event.ImpressionEvent;
import top.openadexchange.tracking.domain.model.AntiFraudContext;

public class AntiFraudContextFactory {

    public static AntiFraudContext forClickEvent(ClickEvent clickEvent, HttpServletRequest request) {
        return null;
    }

    public static AntiFraudContext forImpressionEvent(ImpressionEvent impressionEvent, HttpServletRequest request) {
        return null;
    }

    public static AntiFraudContext forTackingToken(TrackToken tk, HttpServletRequest request) {
        return null;
    }
}
