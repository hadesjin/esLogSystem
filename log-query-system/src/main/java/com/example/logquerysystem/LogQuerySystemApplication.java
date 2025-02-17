package com.example.logquerysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.logquerysystem.mapper")
public class LogQuerySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogQuerySystemApplication.class, args);
    }
}