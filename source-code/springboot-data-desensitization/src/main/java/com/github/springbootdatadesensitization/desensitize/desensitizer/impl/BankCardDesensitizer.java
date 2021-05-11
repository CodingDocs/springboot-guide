package com.github.springbootdatadesensitization.desensitize.desensitizer.impl;

import com.github.springbootdatadesensitization.desensitize.desensitizer.AbstractDesensitizer;

/**
 * 银行卡号脱敏
 *
 * @author Guide哥
 * @date 2021/05/10 20:28
 **/
public class BankCardDesensitizer extends AbstractDesensitizer {
    public BankCardDesensitizer() {
        super(3, 3);
    }
}
