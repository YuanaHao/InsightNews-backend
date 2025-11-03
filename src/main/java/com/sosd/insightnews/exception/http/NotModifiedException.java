package com.sosd.insightnews.exception.http;

import org.springframework.http.HttpStatus;

public class NotModifiedException extends AbstractInsightNewsHttpException {
    public NotModifiedException(String msgTpl, Object... args) {
        super(msgTpl, args);
        setHttpStatus(HttpStatus.NOT_MODIFIED);
    }
}
