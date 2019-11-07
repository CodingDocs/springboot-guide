package com.example.beanvalidationdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber,String> {

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        if (phoneField == null) {
            // can be null
            return true;
        }
        return phoneField.matches("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$") && phoneField.length() > 8 && phoneField.length() < 14;
    }
}
