package com.nutritiondesigner.userservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class controller {

    private final Environment env;

    @GetMapping
    public String test() {
        return "Welcome to user service";
    }

    @GetMapping("/filter-test")
    public String gateWayFilterTest(@RequestHeader("user-request") String header) {
        return header;
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());

        return String.format("This is a message from User Sevice on Port %s", env.getProperty("local.server.port"));
    }
}
