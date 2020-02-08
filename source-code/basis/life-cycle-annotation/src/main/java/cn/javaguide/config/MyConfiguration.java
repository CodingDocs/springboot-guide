package cn.javaguide.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class MyConfiguration {
    public MyConfiguration() {
        System.out.println("构造方法被调用");
    }

    @PostConstruct
    private void init() {
        System.out.println("PostConstruct注解方法被调用");
    }

    @PreDestroy
    private void shutdown() {
        System.out.println("PreDestroy注解方法被调用");
    }
}

