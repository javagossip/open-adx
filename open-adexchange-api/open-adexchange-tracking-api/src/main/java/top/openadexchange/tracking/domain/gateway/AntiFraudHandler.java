package top.openadexchange.tracking.domain.gateway;

import org.springframework.core.Ordered;

import top.openadexchange.tracking.domain.model.AntiFraudContext;

public interface AntiFraudHandler extends Ordered {

    void handle(AntiFraudContext antiFraudContext);
}
