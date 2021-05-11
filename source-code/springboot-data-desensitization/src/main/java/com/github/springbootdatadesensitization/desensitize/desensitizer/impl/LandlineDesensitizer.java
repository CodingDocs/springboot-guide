package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 座机号脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:28
 **/
public class LandlineDesensitizer extends AbstractDesensitizer {
    public LandlineDesensitizer() {
        super(2, 2);
    }
}
