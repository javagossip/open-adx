package top.openadexchange.openapi.dsp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.openadexchange.commons.exception.ValidateException;
import top.openadexchange.openapi.dsp.auth.exception.AuthException;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;
import top.openadexchange.openapi.dsp.commons.OpenApiResponse;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理ValidateException异常
     */
    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.OK)
    public OpenApiResponse<Void> handleValidateException(ValidateException e) {
        log.error("参数校验异常: code={}, message={}", e.getCode(), e.getMessage());
        String code = e.getCode() != null ? e.getCode() : "A0100";
        return OpenApiResponse.error(code, e.getMessage());
    }

    /**
     * 处理AuthException异常
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.OK)
    public OpenApiResponse<Void> handleAuthException(AuthException e) {
        log.error("认证异常: message={}", e.getMessage());
        return OpenApiResponse.error(ApiErrorCode.AUTH_FAILED.getCode(), e.getMessage());
    }

    /**
     * 处理@Valid注解校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public OpenApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常: field={}, message={}", fieldError != null ? fieldError.getField() : "unknown", message);
        return OpenApiResponse.error("A0101", message);
    }

    /**
     * 处理@Validated注解校验异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public OpenApiResponse<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常: field={}, message={}", fieldError != null ? fieldError.getField() : "unknown", message);
        return OpenApiResponse.error("A0102", message);
    }

    /**
     * 处理IllegalArgumentException异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public OpenApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: message={}", e.getMessage());
        return OpenApiResponse.error("A0103", e.getMessage());
    }

    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public OpenApiResponse<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return OpenApiResponse.error("A9999", "系统异常，请稍后重试");
    }
}
