package cn.javaguide.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration2 implements InitializingBean, DisposableBean {

    public MyConfiguration2() {
        System.out.println("构造方法被调用");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet方法被调用");
    }

    @Override
    public void destroy() {
        System.out.println("destroy方法被调用");
    }
}
