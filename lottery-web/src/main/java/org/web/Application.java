package org.web;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.web.controller","org.web.service.impl","org.web.filter","org.web.servlet"})
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@MapperScan(basePackages = "org.web.mapper")
public class Application {

    public static void main(String[] args) {
//    	System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Application.class, args);
    }
}