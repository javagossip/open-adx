package top.openadexchange.tracking.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface AdDedupService {

    boolean tryAddImpression(String impId);

    boolean tryAddClick(String clkId);

    boolean containsImpression(String impId);

    boolean containsClick(String clkId);
}
