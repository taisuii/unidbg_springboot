package com.bytedance.frameworks.core.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Spring {

    public static void main(String[] args) {
        SpringApplication.run(Spring.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(name = "name", defaultValue = "World") String name) {
        Sign test = new Sign();
        String result = test.getSign("", "", "");
        System.out.println(result);
        test.destroy();
        return result;
    }
}