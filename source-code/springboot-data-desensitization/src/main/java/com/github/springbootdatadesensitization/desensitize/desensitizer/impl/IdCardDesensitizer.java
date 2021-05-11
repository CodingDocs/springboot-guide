package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 身份证号脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:28
 **/
public class IdCardDesensitizer extends AbstractDesensitizer {
    public IdCardDesensitizer() {
        super(3, 4);
    }
}
