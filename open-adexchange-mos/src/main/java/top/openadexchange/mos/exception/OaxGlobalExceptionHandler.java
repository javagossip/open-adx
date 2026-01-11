package top.openadexchange.mos.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import top.openadexchange.commons.exception.ValidateException;
import top.openadexchange.dto.commons.ApiResponse;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(basePackages = "top.openadexchange")
public class OaxGlobalExceptionHandler {

    /**
     * 处理ValidateException异常
     */
    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleValidateException(ValidateException e) {
        log.error("参数校验异常: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理@Valid注解校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        String fieldName = fieldError != null ? fieldError.getField() : "unknown";
        log.error("参数校验异常: field={}, message={}", fieldName, message);
        return ApiResponse.error(400, String.format("字段[%s]校验失败: %s", fieldName, message));
    }

    /**
     * 处理@Validated注解校验异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        String fieldName = fieldError != null ? fieldError.getField() : "unknown";
        log.error("参数绑定异常: field={}, message={}", fieldName, message);
        return ApiResponse.error(400, String.format("字段[%s]绑定失败: %s", fieldName, message));
    }

    /**
     * 处理IllegalArgumentException异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: message={}", e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ApiResponse.error(500, "系统异常，请稍后重试");
    }
}
