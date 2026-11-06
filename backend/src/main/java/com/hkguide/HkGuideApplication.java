package com.hkguide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hkguide.mapper")
public class HkGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(HkGuideApplication.class, args);
    }
}
