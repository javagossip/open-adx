package top.openadexchange.tracking.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import java.util.concurrent.ExecutorService;

@ExtensionPoint
public interface ExecutorFactory {

    ExecutorService getExecutor();
}
