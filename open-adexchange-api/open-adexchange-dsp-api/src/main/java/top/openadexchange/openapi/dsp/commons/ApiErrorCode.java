package top.openadexchange.openapi.dsp.commons;

import top.openadexchange.commons.exception.ErrorCode;

public enum ApiErrorCode implements ErrorCode {
    ADVERTISER_ID_IS_REQUIRED("A0012", "dsp平台广告主ID不能为空"),
    COMPANY_NAME_IS_REQUIRED("A0013", "公司名称不能为空"),
    INDUSTRY_CODE_IS_REQUIRED("A0014", "行业代码不能为空"),
    ADVERTISER_NAME_IS_REQUIRED("A0015", "广告主名称不能为空"),
    BUSINESS_LICENSE_NO_IS_REQUIRED("A0016", "营业执照编号不能为空"),
    BUSINESS_LICENSE_URL_IS_REQUIRED("A0017", "营业执照图片不能为空"),

    ADVERTISER_NOT_EXIST("A0002", "广告主不存在"),
    ADVERTISER_EXISTS("A0011", "广告主已存在");

    private final String code;
    private final String message;

    ApiErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
