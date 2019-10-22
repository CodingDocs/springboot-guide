package com.twuc.webApp.exception;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shuang.kou
 */
public abstract class BaseException extends RuntimeException {
    private final ErrorCode error;
    private final HashMap<String, Object> data = new HashMap<>();

    public BaseException(ErrorCode error, Map<String, Object> data) {
        super(format(error.getCode(), error.getMessage(), data));
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    protected BaseException(ErrorCode error, Map<String, Object> data, Throwable cause) {
        super(format(error.getCode(), error.getMessage(), data), cause);
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    private static String format(Integer code, String message, Map<String, Object> data) {
        return String.format("[%d]%s:%s.", code, message, ObjectUtils.isEmpty(data) ? "" : data.toString());
    }

    public ErrorCode getError() {
        return error;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
