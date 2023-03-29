package com.franky.community;

import javax.annotation.PostConstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication //表示这是一个配置类
public class FrankyCommunityApplication {

    @PostConstruct //在构造器调用完以后执行
    public void init() {
        // 解决netty启动冲突问题
        // see in Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    //自动创建了tomcat和底层容器，并自动装配类
    public static void main(String[] args) {
        SpringApplication.run(FrankyCommunityApplication.class, args);
    }

}
