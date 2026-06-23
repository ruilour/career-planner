package com.ruilour.careerplanner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "com.ruilour.careerplanner")
//@SpringBootApplication
//@MapperScan("com.ruilour.careerplanner.mapper")   // ← 新增以便扫描mapperMapper 接口！
public class CareerPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerPlannerApplication.class, args);
    }

}
