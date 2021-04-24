package com.example.beanvalidationdemo;


import com.example.beanvalidationdemo.entity.PersonRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 验证出现参数不合法的情况抛出异常并且可以正确被捕获
     */
    @Test
    public void should_check_person_value() throws Exception {
        PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                .classId("82938390")
                .region("Shanghai")
                .phoneNumber("1816313815").build();
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.jsonPath("sex").value("sex 值不在可选范围"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("name 不能为空"))
                .andExpect(MockMvcResultMatchers.jsonPath("region").value("Region 值不在可选范围内"))
                .andExpect(MockMvcResultMatchers.jsonPath("phoneNumber").value("phoneNumber 格式不正确"));
    }

    @Test
    public void should_check_path_variable() throws Exception {
        mockMvc.perform(get("/api/persons/6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("getPersonByID.id: 超过 id 的范围了"));
    }

    @Test
    public void should_check_request_param_value2() throws Exception {
        mockMvc.perform(put("/api/persons")
                .param("name", "snailclimbsnailclimb")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("getPersonByName.name: 超过 name 的范围了"));
    }

    /**
     * 手动校验对象
     */
    @Test
    public void check_person_manually() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                .classId("82938390").build();
        Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);
        violations.forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
    }
}
