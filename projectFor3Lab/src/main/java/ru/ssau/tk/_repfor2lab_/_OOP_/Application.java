package ru.ssau.tk._repfor2lab_._OOP_;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.ssau.tk._repfor2lab_._OOP_.repositories")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}