package com.example.beanvalidationdemo.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";

    Class[] groups() default {};

    Class[] payload() default {};
}
