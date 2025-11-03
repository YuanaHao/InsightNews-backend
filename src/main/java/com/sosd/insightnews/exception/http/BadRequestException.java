package com.sosd.insightnews.exception.http;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractInsightNewsHttpException {
    public BadRequestException(String msgtpl, Object... args) {
        super(msgtpl, args);
        setHttpStatus(HttpStatus.BAD_REQUEST);
    }

}
