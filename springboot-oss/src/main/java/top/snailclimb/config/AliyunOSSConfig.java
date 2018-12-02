package top.snailclimb.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@PropertySource(value = "classpath:application-oss.properties")
@ConfigurationProperties(prefix = "aliyun.oss")
/**
 * 阿里云oss的配置类
 */
public class AliyunOSSConfig {
    private String bucketname;
    private String endpoint;
    private String keyid;
    private String keysecret;
    private String filehost;
}
