package top.snailclimb.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import top.snailclimb.service.HelloService;

@Component
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
