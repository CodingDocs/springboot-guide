package com.example.beanvalidationdemo.entity;

import com.example.beanvalidationdemo.validation.PhoneNumber;
import com.example.beanvalidationdemo.validation.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 *
 * @author Guide哥
 * @date 2021/04/21 20:48
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest {

    @NotNull(message = "classId 不能为空")
    private String classId;

    @Size(max = 33)
    @NotNull(message = "name 不能为空")
    private String name;

    @Pattern(regexp = "(^Man$|^Woman$|^UGM$)", message = "sex 值不在可选范围")
    @NotNull(message = "sex 不能为空")
    private String sex;

    @Region
    private String region;

    @PhoneNumber(message = "phoneNumber 格式不正确")
    @NotNull(message = "phoneNumber 不能为空")
    private String phoneNumber;

}
