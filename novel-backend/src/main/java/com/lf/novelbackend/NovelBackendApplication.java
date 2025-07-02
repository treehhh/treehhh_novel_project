package com.lf.novelbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.lf.novelbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class NovelBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelBackendApplication.class, args);
    }

}
