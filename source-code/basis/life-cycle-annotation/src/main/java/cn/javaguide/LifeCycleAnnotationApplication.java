package cn.javaguide;

import cn.javaguide.config.MyConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LifeCycleAnnotationApplication {

    public static void main(String[] args) {
        MyConfiguration myConfiguration = new MyConfiguration();
        MyConfiguration myConfiguration2 = new MyConfiguration();
        SpringApplication.run(LifeCycleAnnotationApplication.class, args);
    }
}
