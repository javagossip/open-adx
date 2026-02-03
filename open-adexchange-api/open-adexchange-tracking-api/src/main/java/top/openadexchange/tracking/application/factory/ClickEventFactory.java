package top.openadexchange.tracking.application.factory;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.dto.event.ClickEvent;
import top.openadexchange.tracking.utils.RequestUtils;

/**
 * 点击事件工厂
 */
public class ClickEventFactory {

    public static ClickEvent of(TrackToken tk, HttpServletRequest request) {
        String eventId = UUID.randomUUID().toString().replace("-", "");
        String clickId = UUID.randomUUID().toString().replace("-", "");
        long eventTime = System.currentTimeMillis();
        String clientIp = RequestUtils.getClientIp(request);

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setEventId(eventId);
        clickEvent.setClickId(clickId);
        clickEvent.setImpId(tk.getImpId());
        clickEvent.setReqId(tk.getReqId());
        clickEvent.setPublisherId(tk.getPublisherId());
        clickEvent.setAdSlotId(tk.getAdSlotId());
        clickEvent.setCrid(tk.getCrid());
        clickEvent.setAdvId(tk.getAdvId());
        clickEvent.setDspId(tk.getDspId());
        clickEvent.setPrice(tk.getPrice());
        clickEvent.setPriceMode(tk.getPriceMode());
        clickEvent.setUa(tk.getUa());
        clickEvent.setClientIp(clientIp);
        clickEvent.setIpv6(tk.getIpv6());
        clickEvent.setOs(tk.getOs());
        clickEvent.setOsv(tk.getOsv());
        clickEvent.setEventTime(eventTime);
        return clickEvent;
    }
}
