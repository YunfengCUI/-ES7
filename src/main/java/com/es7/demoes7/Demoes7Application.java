package com.es7.demoes7;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.es7.demoes7.mapper")
public class Demoes7Application {

    public static void main(String[] args) {
//        SpringApplication springApplication = new SpringApplication(Demoes7Application.class);
//        //Banner.Mode.OFF 关闭
//        springApplication.setBannerMode(Banner.Mode.OFF);
        SpringApplication.run(Demoes7Application.class, args);

    }

}
