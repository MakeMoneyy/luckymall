package com.example.smartcustomerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 智能客服系统应用程序入口
 */
@SpringBootApplication
@EnableCaching
public class SmartCustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCustomerServiceApplication.class, args);
    }
} 