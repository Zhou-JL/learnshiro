package com.zhoujl.learnshiro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.zhoujl.learnshiro.mapper")
public class LearnshiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnshiroApplication.class, args);
    }

}
