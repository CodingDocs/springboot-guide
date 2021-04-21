package com.example.beanvalidationdemo;

import com.example.beanvalidationdemo.entity.Person;
import com.example.beanvalidationdemo.entity.PersonRequest;
import com.example.beanvalidationdemo.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {
    @Autowired
    private PersonService service;

    @Test
    public void should_throw_exception_when_person_request_is_not_valid() {
        try {
            PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                    .classId("82938390").build();
            service.validatePersonRequest(personRequest);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_check_person_with_groups() {
        Person person = new Person();
        person.setGroup("group1");
        service.validatePersonGroupForAdd(person);
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_check_person_with_groups2() {
        Person person = new Person();
        service.validatePersonGroupForDelete(person);
    }

}
