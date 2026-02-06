package top.openadexchange.openapi.ssp.application.dto;

import top.openadexchange.constants.enums.DeviceType;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest.Device;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest.Imp;

/**
 * 索引构建请求
 *
 * @param adSlotId 广告位id
 * @param os 操作系统: iOS, Android
 * @param deviceType 设备类型: PHONE,PAD,PC,TV
 * @param ip 用户ip
 * @author weiping
 * @since 2025-12-13
 */
public record BuildIndexRequest(String adSlotId, String os, String deviceType, String ip) {

    public static BuildIndexRequest of(AdGetRequest adGetRequest) {
        Imp imp = adGetRequest.getImp().getFirst();
        Device device = adGetRequest.getDevice();

        return new BuildIndexRequest(imp.getTagid(),
                device.getOs(),
                DeviceType.fromValue(device.getDeviceType()).name(),
                device.getIp());
    }
}
