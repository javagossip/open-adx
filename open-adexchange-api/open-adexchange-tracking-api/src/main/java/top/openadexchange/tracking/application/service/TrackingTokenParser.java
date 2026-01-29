package top.openadexchange.tracking.application.service;

import java.util.Base64;

import com.alibaba.fastjson2.JSON;

import top.openadexchange.commons.EnvUtils;
import top.openadexchange.commons.hash.HmacUtils;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.tracking.domain.model.TrackTokenParseResult;

public class TrackingTokenParser {

    private static final String TK_SECRET_KEY = "oax.tracking.secret";

    public static TrackTokenParseResult parse(String tk) {
        String secretKey = EnvUtils.getProperty(TK_SECRET_KEY);
        String[] tkSegments = tk.split("\\.");

        if (tkSegments.length < 2) {
            throw new IllegalArgumentException("tk is invalid");
        }
        String payloadBase64 = tkSegments[0];
        String signature = tkSegments[1];

        TrackTokenParseResult trackTokenParseResult = new TrackTokenParseResult();
        trackTokenParseResult.setValid(true);
        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadBase64);
            TrackToken trackToken = JSON.parseObject(payloadBytes, TrackToken.class);
            trackTokenParseResult.setData(trackToken);

            String newSignature = HmacUtils.hmacSha256(secretKey, payloadBytes);
            if (!newSignature.equals(signature)) {
                trackTokenParseResult.error("signature is invalid");
            }
            if (System.currentTimeMillis() > trackToken.getExpireAt()) {
                trackTokenParseResult.error("imp is expired");
            }
            return trackTokenParseResult;
        } catch (Exception ex) {
            throw new IllegalArgumentException("tk is invalid");
        }
    }
}
