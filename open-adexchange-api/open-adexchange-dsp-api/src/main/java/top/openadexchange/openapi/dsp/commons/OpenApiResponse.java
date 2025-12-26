package top.openadexchange.openapi.dsp.commons;

import lombok.Data;

@Data
public class OpenApiResponse<T> {

    private String code;
    private String message;
    private T data;

    private static String SUCCESS_CODE = "A0000";

    public OpenApiResponse() {
    }

    public OpenApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public OpenApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> OpenApiResponse<T> success(T t) {
        return new OpenApiResponse<>(SUCCESS_CODE, "OK", t);
    }

    public static <T> OpenApiResponse<T> error(String code, String message) {
        return new OpenApiResponse<>(code, message);
    }
}
