package top.snailclimb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//此注解表示SpringBoot启动类
@SpringBootApplication
public class //@MapperScan("top.snailclimb.dao")
MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
