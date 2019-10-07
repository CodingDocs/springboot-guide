package com.example.beanvalidationdemo.entity;

import com.example.beanvalidationdemo.constants.Constants;
import com.example.beanvalidationdemo.service.AddPersonGroup;
import com.example.beanvalidationdemo.service.DeletePersonGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @NotNull(message = "classId 不能为空")
    private String classId;

    @Size(max = 33)
    @NotNull(message = "name 不能为空")
    private String name;

    @Pattern(regexp = Constants.sexs, message = "sex 值不在可选范围")
    @NotNull(message = "sex 不能为空")
    private String sex;

    @Email(message = "email 格式不正确")
    @NotNull(message = "email 不能为空")
    private String email;

    @Region
    private String region;

    @NotNull(groups = DeletePersonGroup.class)
    @Null(groups = AddPersonGroup.class)
    private String group;
}
