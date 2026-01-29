package top.openadexchange.tracking.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import top.openadexchange.tracking.domain.gateway.AntiFraudHandler;
import top.openadexchange.tracking.domain.model.AntiFraudContext;

@Service
public class AntiFraudService {

    private final List<AntiFraudHandler> antiFraudHandlers;

    public AntiFraudService(List<AntiFraudHandler> antiFraudHandlers) {
        this.antiFraudHandlers = new ArrayList<>(antiFraudHandlers);
        this.antiFraudHandlers.sort(Comparator.comparingInt(AntiFraudHandler::getOrder));
    }

    public void handle(AntiFraudContext antiFraudContext) {
        for (AntiFraudHandler handler : antiFraudHandlers) {
            handler.handle(antiFraudContext);
        }
    }
}
