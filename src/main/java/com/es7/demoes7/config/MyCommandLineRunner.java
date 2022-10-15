package com.es7.demoes7.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//告诉系统这个是一个spring工具类
@Component
@Order(value = 1)
public class MyCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("CUI YUN FENG PROJECT RUN SUCCESS!!!!!!!!!!");
    }
}
