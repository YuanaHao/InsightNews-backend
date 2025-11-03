package com.sosd.insightnews.exception.http;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractInsightNewsHttpException {

    public UnauthorizedException(String msgTpl, Object... args) {
        super(msgTpl, args);
        setHttpStatus(HttpStatus.UNAUTHORIZED);
    }
}
