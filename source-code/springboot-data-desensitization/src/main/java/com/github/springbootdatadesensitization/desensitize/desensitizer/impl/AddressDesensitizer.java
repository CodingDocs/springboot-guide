package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 地址脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:15
 **/
public class AddressDesensitizer extends AbstractDesensitizer {
    public AddressDesensitizer() {
        super(3, 3);
    }
}
