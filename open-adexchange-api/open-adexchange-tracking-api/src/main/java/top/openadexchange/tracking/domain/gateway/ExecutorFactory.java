package top.openadexchange.tracking.domain.gateway;

import java.util.concurrent.ExecutorService;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface ExecutorFactory {

    ExecutorService getExecutor();
}
