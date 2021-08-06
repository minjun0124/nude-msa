package com.nutritiondesigner.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
public class controller {

    @GetMapping
    public String test() {
        return "Welcome to user service";
    }

    @GetMapping("/filter-test")
    public String gateWayFilterTest(@RequestHeader("user-request") String header) {
        return header;
    }

}
