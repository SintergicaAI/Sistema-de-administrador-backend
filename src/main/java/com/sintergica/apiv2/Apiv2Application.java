package com.sintergica.apiv2;

import com.sintergica.apiv2.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Apiv2Application implements CommandLineRunner {

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  public static void main(String[] args) {
    SpringApplication.run(Apiv2Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {



  }
}
