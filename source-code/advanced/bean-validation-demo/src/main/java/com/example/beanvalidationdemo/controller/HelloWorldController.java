package com.example.beanvalidationdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shuang.kou
 * 验证基本环境搭建是否正确
 */
@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
