package top.openadexchange.openapi.ssp.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class IndexKeys {

    private List<String> osKeys; //操作系统索引key
    private List<String> deviceTypeKeys; //设备类型索引Key
    private List<String> tagIdKeys; //广告位ID索引key
    private List<String> regionKeys; //地域索引key
}
