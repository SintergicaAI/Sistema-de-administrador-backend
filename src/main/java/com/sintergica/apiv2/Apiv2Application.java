package com.sintergica.apiv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.sintergica.apiv2")
public class Apiv2Application {
    public static void main(String[] args) {
        SpringApplication.run(Apiv2Application.class, args);
    }
}
