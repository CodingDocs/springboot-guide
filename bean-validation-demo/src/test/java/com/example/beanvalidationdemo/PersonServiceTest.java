package com.example.beanvalidationdemo;

import com.example.beanvalidationdemo.entity.Person;
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

    @Test(expected = ConstraintViolationException.class)
    public void should_throw_exception_when_person_is_not_valid() {
        Person person = new Person();
        person.setSex("Man22");
        person.setClassId("82938390");
        person.setEmail("SnailClimb");
        service.validatePerson(person);
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_check_person_with_groups() {
        Person person = new Person();
        person.setSex("Man22");
        person.setClassId("82938390");
        person.setEmail("SnailClimb");
        person.setGroup("group1");
        service.validatePersonGroupForAdd(person);
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_check_person_with_groups2() {
        Person person = new Person();
        person.setSex("Man22");
        person.setClassId("82938390");
        person.setEmail("SnailClimb");
        service.validatePersonGroupForDelete(person);
    }

}
