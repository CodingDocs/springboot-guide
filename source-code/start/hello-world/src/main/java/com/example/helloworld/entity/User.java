package com.example.helloworld.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    Long id;
    String name;
    String email;
    String password;
}
