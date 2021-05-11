package com.github.springbootdatadesensitization.desensitize.exception;

import lombok.Getter;

/**
 * @author shuang.kou
 * @date 2017/12/19 14:25
 */
@Getter
public class DesensitizationException extends RuntimeException {
    public DesensitizationException(String message) {
        super(message);
    }
}