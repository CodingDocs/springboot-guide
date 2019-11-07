package com.example.beanvalidationdemo;


import com.example.beanvalidationdemo.entity.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_get_person() throws Exception {
        Person person = new Person();
        person.setName("SnailClimb");
        person.setSex("Man");
        person.setClassId("82938390");
        person.setEmail("Snailclimb@qq.com");
        person.setRegion("China");
        person.setPhoneNumber("13615833391");


        mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("SnailClimb"))
                .andExpect(MockMvcResultMatchers.jsonPath("classId").value("82938390"))
                .andExpect(MockMvcResultMatchers.jsonPath("sex").value("Man"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("Snailclimb@qq.com"));
        ;
    }

    @Test
    public void should_check_person_value() throws Exception {
        Person person = new Person();
        person.setSex("Man22");
        person.setClassId("82938390");
        person.setEmail("SnailClimb");
        person.setRegion("BeiGuo");
        person.setPhoneNumber("1361583339");

        mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(MockMvcResultMatchers.jsonPath("sex").value("sex 值不在可选范围"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("name 不能为空"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("email 格式不正确"))
                .andExpect(MockMvcResultMatchers.jsonPath("region").value("Region 值不在可选范围内"))
                .andExpect(MockMvcResultMatchers.jsonPath("phoneNumber").value("phoneNumber 格式不正确"));

    }

    @Test
    public void should_check_param_value() throws Exception {

        mockMvc.perform(get("/api/person/6")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("getPersonByID.id: 超过 id 的范围了"));
    }

    @Test
    public void should_check_param_value2() throws Exception {

        mockMvc.perform(put("/api/person")
                .param("name", "snailclimbsnailclimb")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("getPersonByName.name: 超过 name 的范围了"));
    }

    /**
     * 手动校验对象，很多场景下需要使用这种方式
     */
    @Test
    public void check_person_manually() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Person person = new Person();
        person.setSex("Man22");
        person.setClassId("82938390");
        person.setEmail("SnailClimb");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        //output:
        //email 格式不正确
        //name 不能为空
        //sex 值不在可选范围
        for (ConstraintViolation<Person> constraintViolation : violations) {
            System.out.println(constraintViolation.getMessage());
        }
    }
}
