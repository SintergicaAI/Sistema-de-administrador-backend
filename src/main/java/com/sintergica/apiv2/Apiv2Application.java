package com.sintergica.apiv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.DelegatingFilterProxy;

@SpringBootApplication
public class Apiv2Application {
    public static void main(String[] args) {
        SpringApplication.run(Apiv2Application.class, args);
    }

    /*@Bean
    public DelegatingFilterProxy jwtFiltere() {
        return new DelegatingFilterProxy("jwtFilter");
    }*/

}
