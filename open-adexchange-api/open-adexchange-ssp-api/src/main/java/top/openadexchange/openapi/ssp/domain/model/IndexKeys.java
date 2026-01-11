package top.openadexchange.openapi.ssp.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class IndexKeys {

    private List<String> osKeys; //操作系统
    private List<String> deviceTypeKeys; //设备类型
    private List<String> tagIdKeys; //广告位ID
    private List<String> regionKeys; //地域
}
