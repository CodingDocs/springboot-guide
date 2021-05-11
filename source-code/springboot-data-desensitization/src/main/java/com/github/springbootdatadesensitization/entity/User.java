package com.github.springbootdatadesensitization.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.springbootdatadesensitization.desensitize.annotation.JsonDesensitization;
import com.github.springbootdatadesensitization.desensitize.enums.DesensitizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @JsonDesensitization(DesensitizationType.EMAIL)
    private String email;

    @JsonDesensitization(DesensitizationType.ID_CARD)
    private String idCard;

    @JsonDesensitization(DesensitizationType.PASSWORD)
    private String password;

    @JsonDesensitization(DesensitizationType.MOBILE)
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    @JsonDesensitization(DesensitizationType.BIRTHDAY)
    private Date birthday;
}
