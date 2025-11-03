package com.sosd.insightnews.exception;


import com.sosd.insightnews.domain.R;
import com.sosd.insightnews.exception.http.AbstractInsightNewsHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义业务异常
    @ExceptionHandler(AbstractInsightNewsHttpException.class)
    @ResponseBody
    public R<Object> handleCustomException(AbstractInsightNewsHttpException ex, WebRequest request) {
        log.error("Custom Service Error: ", ex);
        return R.fail(ex.getHttpStatus().value(), ex.getMessage());
    }

    // 处理系统级异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Internal Server Error: ", ex);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统繁忙，请稍后重试");
    }
}
