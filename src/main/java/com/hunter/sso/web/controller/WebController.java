package com.hunter.sso.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hunter
 * @date 2020/4/20
 */
@RestController
public class WebController {

    @GetMapping("/hello")
    public String hello(){
        return "hello sso web test";
    }
}
