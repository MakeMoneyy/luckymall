package com.luckymall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 招财商城主应用类
 * 
 * @author LuckyMall Team
 */
@SpringBootApplication
@MapperScan("com.luckymall.mapper")
public class LuckyMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuckyMallApplication.class, args);
        System.out.println("招财商城后端服务启动成功！");
        System.out.println("访问地址：http://localhost:8080");
    }
} 