package github.javaguide.springbootscheduletask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author shuang.kou
 */
@Component
@EnableAsync
public class AsyncScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(AsyncScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private List<Integer> index = Arrays.asList(6, 6, 2, 3);

    int i = 0;

    /**
     * fixedDelay：固定延迟执行。距离上一次调用成功后2秒才执。
     */
    @Async
    @Scheduled(fixedDelay = 2000)
    public void reportCurrentTimeWithFixedDelay() {
        log.info("Current Thread : {}", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(3);
            log.info("Fixed Delay Task : The time is now {}", dateFormat.format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@Async
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTimeWithFixedRate() {
        if (i == 0) {
            log.info("start time is {}", dateFormat.format(new Date()));
        }
        if (i < 4) {
            try {
                TimeUnit.SECONDS.sleep(index.get(i));
                log.info("Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
