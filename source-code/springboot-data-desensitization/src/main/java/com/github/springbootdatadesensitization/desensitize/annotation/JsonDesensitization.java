package com.github.springbootdatadesensitization.desensitize.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.springbootdatadesensitization.desensitize.JsonDesensitizationSerializer;
import com.github.springbootdatadesensitization.desensitize.enums.DesensitizationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 *
 * @author Guide哥
 * @date 2021/05/10 20:36
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = JsonDesensitizationSerializer.class)
public @interface JsonDesensitization {
    DesensitizationType value();
}
