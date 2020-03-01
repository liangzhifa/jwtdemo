package com.zhifa.jwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWTController {
    @GetMapping("/hi")
    public String innerMsg(){
        return "you get the message!";
    }
}
