package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 手机号脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:28
 **/
public class MobileDesensitizer extends AbstractDesensitizer {
    public MobileDesensitizer() {
        super(3, 4);
    }
}
