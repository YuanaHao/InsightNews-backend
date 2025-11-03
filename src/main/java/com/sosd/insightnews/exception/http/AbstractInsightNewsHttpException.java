package com.sosd.insightnews.exception.http;


import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public abstract class AbstractInsightNewsHttpException extends RuntimeException {
    private static final long serialVersionUID = -1713129594004951820L;

    protected HttpStatus httpStatus;

    /**
     * When args not empty, use format(String, Object...)}
     * to replace %s in msgTpl with args to set the error message. Otherwise, use msgTpl
     * to set the error message. e.g.:
     * <pre>{@code new NotFoundException("... %s ... %s ... %s", "str", 0, 0.1)}</pre>
     * If the number of '%s' in `msgTpl` does not match args length, the '%s' string will be printed.
     */
    public AbstractInsightNewsHttpException(String msgTpl, Object... args){
        super(args == null || args.length == 0 ? msgTpl : String.format(msgTpl, args));
    }

    public AbstractInsightNewsHttpException(String msg, Exception e){
        super(msg,e);
    }

    protected void setHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }

}