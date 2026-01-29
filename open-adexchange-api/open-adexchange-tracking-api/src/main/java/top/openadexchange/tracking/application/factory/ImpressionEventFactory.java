package top.openadexchange.tracking.application.factory;

import jakarta.servlet.http.HttpServletRequest;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.tracking.domain.event.EventType;
import top.openadexchange.tracking.domain.event.ImpressionEvent;
import top.openadexchange.tracking.utils.RequestUtils;

import java.util.UUID;

/**
 * 曝光事件工厂
 */
public class ImpressionEventFactory {

    public static ImpressionEvent of(TrackToken tk, HttpServletRequest request) {
        String eventId = UUID.randomUUID().toString().replace("-", "");
        long eventTime = System.currentTimeMillis();
        String clientIp = RequestUtils.getClientIp(request);

        ImpressionEvent impEvent = new ImpressionEvent();
        impEvent.setEventId(eventId);
        impEvent.setImpId(tk.getImpId());
        impEvent.setReqId(tk.getReqId());
        impEvent.setPublisherId(tk.getPublisherId());
        impEvent.setAdSlotId(tk.getAdSotId());
        impEvent.setCrid(tk.getCrid());
        impEvent.setAdvId(tk.getAdvId());
        impEvent.setDspId(tk.getDspId());
        impEvent.setPrice(tk.getPrice());
        impEvent.setPriceMode(tk.getPriceMode());
        impEvent.setUa(tk.getUa());
        impEvent.setClientIp(clientIp);
        impEvent.setIpv6(tk.getIpv6());
        impEvent.setOs(tk.getOs());
        impEvent.setOsv(tk.getOsv());
        impEvent.setEventTime(eventTime);
        impEvent.setEventType(EventType.IMPRESSION);
        return impEvent;
    }
}
