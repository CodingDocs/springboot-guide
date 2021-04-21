package com.example.beanvalidationdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        if (phoneField == null) {
            // can be null
            return true;
        }
        //  大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^[1]((3[0-9])|(4[5-9])|(5[0-3,5-9])|([6][5,6])|(7[0-9])|(8[0-9])|(9[1,8,9]))\\d{8}$";
        return phoneField.matches(regExp);
    }
}
