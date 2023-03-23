package com.franky.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //表示这是一个配置类
public class FrankyCommunityApplication {

    //自动创建了tomcat和底层容器，并自动装配类
    public static void main(String[] args) {
        SpringApplication.run(FrankyCommunityApplication.class, args);
    }

}
