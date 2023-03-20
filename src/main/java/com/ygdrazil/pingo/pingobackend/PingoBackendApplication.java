package com.ygdrazil.pingo.pingobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
@RestController
public class PingoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingoBackendApplication.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
        System.out.println(org.hibernate.Version.getVersionString());
        return String.format("Hello %s!", name);
    }
}
