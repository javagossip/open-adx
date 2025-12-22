package top.openadexchange.commons.service;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface EntityCodeGenerator {

    String generateCode();
}
