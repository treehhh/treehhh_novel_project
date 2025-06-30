package com.lf.novelbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lf.novelbackend.mapper")
public class NovelBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelBackendApplication.class, args);
    }

}
