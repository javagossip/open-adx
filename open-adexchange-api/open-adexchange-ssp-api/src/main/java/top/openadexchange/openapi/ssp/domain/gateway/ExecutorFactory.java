package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@ExtensionPoint
public interface ExecutorFactory {

    ExecutorService getExecutor();
}
