package com.twuc.webApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourseNotFoundException2 extends RuntimeException {

    public ResourseNotFoundException2() {
    }

    public ResourseNotFoundException2(String message) {
        super(message);
    }
}
