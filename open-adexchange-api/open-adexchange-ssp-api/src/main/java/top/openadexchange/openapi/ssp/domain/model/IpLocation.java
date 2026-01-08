package top.openadexchange.openapi.ssp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IpLocation {

    private String country; //国家
//    private String region; //区域
    private String province; //省份
    private String city; //城市
    private String isp; //运营商
    private String regionCode; //区域代码
}
