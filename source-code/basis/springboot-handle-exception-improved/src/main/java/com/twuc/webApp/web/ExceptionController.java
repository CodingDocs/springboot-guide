package com.twuc.webApp.web;

import com.google.common.collect.ImmutableMap;
import com.twuc.webApp.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExceptionController {

    @GetMapping("/resourceNotFound")
    public void throwException() {
        throw new ResourceNotFoundException(ImmutableMap.of("person id:", 1L));
    }

}
