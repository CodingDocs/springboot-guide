package com.example.dto2entity;

import com.example.helloworld.entity.User;
import com.example.helloworld.dto.UserDto;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class BeanUtilsTest {

    @Test
    public void should_copy_user_properties_to_userDto() {
        User user = new User(123213L, "Guide哥", "koushuangbwcx@163.com", "123456");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        assertTrue("name copied", userDto.getName().equals(user.getName()));
        assertTrue("email copied", userDto.getEmail().equals(user.getEmail()));
    }

    @Test
    public void should_copy_user_properties_to_userDto_but_not_copy_email() {
        User user = new User(123213L, "Guide哥", "koushuangbwcx@163.com", "123456");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto, "email");
        assertTrue("name copied", userDto.getName().equals(user.getName()));
        assertTrue("email not copied", userDto.getEmail() == null);
    }
}
