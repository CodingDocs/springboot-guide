package github.javaguide.springbootscheduletask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootScheduleTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootScheduleTaskApplication.class, args);
    }

}
