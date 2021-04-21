package com.example.beanvalidationdemo.entity;

import com.example.beanvalidationdemo.service.AddPersonGroup;
import com.example.beanvalidationdemo.service.DeletePersonGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
public class Person {

    // 当验证组为 DeletePersonGroup 的时候 group 字段不能为空
    @NotNull(groups = DeletePersonGroup.class)
    // 当验证组为 AddPersonGroup 的时候 group 字段需要为空
    @Null(groups = AddPersonGroup.class)
    private String group;
}
