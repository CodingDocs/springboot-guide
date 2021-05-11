package com.github.springbootdatadesensitization.desensitize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springbootdatadesensitization.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonDesensitizationSerializerTest {

    @Test
    void should_desensitize_entity() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = User.builder()
                .birthday(new Date())
                .phone("1816313814")
                .email("136148@qq.com")
                .password("12dsfs@dfsdg431")
                .idCard("463827319960397495")
                .build();
        String s = objectMapper.writeValueAsString(user);
        System.out.println(s);
        User desensitizedUser = objectMapper.readValue(s, User.class);
        assertEquals("******@qq.com", desensitizedUser.getEmail());
        assertEquals("463***********7495", desensitizedUser.getIdCard());
        assertEquals("******", desensitizedUser.getPassword());
        assertEquals("181***3814", desensitizedUser.getPhone());

    }
}