package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 密码脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:28
 **/
public class BirthdayDesensitizer extends AbstractDesensitizer {
    public BirthdayDesensitizer() {
        super(4, 0);
    }
}
