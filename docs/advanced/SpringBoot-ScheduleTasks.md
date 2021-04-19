很多时候我们都需要为系统建立一个定时任务来帮我们做一些事情，SpringBoot 已经帮我们实现好了一个，我们只需要直接使用即可，当然你也可以不用   SpringBoot 自带的定时任务，整合 Quartz 很多时候也是一个不错的选择。

本文不涉及 SpringBoot 整合 Quartz 的内容，只演示了如何使用 SpringBoot 自带的实现定时任务的方式。相关代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/springboot-schedule-tast

## Spring Schedule 实现定时任务

我们只需要 SpringBoot 项目最基本的依赖即可，所以这里就不贴配置文件了。

### 1. 创建一个 scheduled task

我们使用 `@Scheduled` 注解就能很方便地创建一个定时任务，下面的代码中涵盖了 `@Scheduled `的常见用法，包括：固定速率执行、固定延迟执行、初始延迟执行、使用 Cron 表达式执行定时任务。

>  Cron 表达式:  主要用于定时作业(定时任务)系统定义执行时间或执行频率的表达式，非常厉害，你可以通过 Cron 表达式进行设置定时任务每天或者每个月什么时候执行等等操作。
>
> 推荐一个在线Cron表达式生成器： [https://crontab.guru/](https://crontab.guru/)

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author shuang.kou
 */
@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * fixedRate：固定速率执行。每5秒执行一次。
     */
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTimeWithFixedRate() {
        log.info("Current Thread : {}", Thread.currentThread().getName());
        log.info("Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
    }

    /**
     * fixedDelay：固定延迟执行。距离上一次调用成功后2秒才执。
     */
    @Scheduled(fixedDelay = 2000)
    public void reportCurrentTimeWithFixedDelay() {
        try {
            TimeUnit.SECONDS.sleep(3);
            log.info("Fixed Delay Task : The time is now {}", dateFormat.format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialDelay:初始延迟。任务的第一次执行将延迟5秒，然后将以5秒的固定间隔执行。
     */
    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void reportCurrentTimeWithInitialDelay() {
        log.info("Fixed Rate Task with Initial Delay : The time is now {}", dateFormat.format(new Date()));
    }

    /**
     * cron：使用Cron表达式。　每分钟的1，2秒运行
     */
    @Scheduled(cron = "1-2 * * * * ? ")
    public void reportCurrentTimeWithCronExpression() {
        log.info("Cron Expression: The time is now {}", dateFormat.format(new Date()));
    }
}

```

关于 fixedRate 这里其实有个坑，假如我们有这样一种情况：我们某个方法的定时器设定的固定速率是每5秒执行一次。这个方法现在要执行下面四个任务，四个任务的耗时是：6 s、6s、 2s、 3s，请问这些任务默认情况下（单线程）将如何被执行？

我们写一段简单的程序验证：

```java
    private static final Logger log = LoggerFactory.getLogger(AsyncScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private List<Integer> index = Arrays.asList(6, 6, 2, 3);
    int i = 0;
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTimeWithFixedRate() {
        if (i == 0) {
            log.info("Start time is {}", dateFormat.format(new Date()));
        }
        if (i < 5) {
            try {
                TimeUnit.SECONDS.sleep(index.get(i));
                log.info("Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
```

运行程序输出如下：

```java
Start time is 20:58:33
Fixed Rate Task : The time is now 20:58:39
Fixed Rate Task : The time is now 20:58:45
Fixed Rate Task : The time is now 20:58:47
Fixed Rate Task : The time is now 20:58:51
```

 看下面的运行任务示意图应该很好理解了。

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/fixdrate-single-thread.png)

如果我们将这个方法改为并行运行，运行结果就截然不同了。

###  2. 启动类上加上`@EnableScheduling`注解

在 SpringBoot 中我们只需要在启动类上加上`@EnableScheduling`便可以启动定时任务了。

```java
@SpringBootApplication
@EnableScheduling
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 3. 自定义线程池执行 scheduled task

默认情况下，`@Scheduled`任务都在Spring创建的大小为1的默认线程池中执行，你可以通过在加了`@Scheduled`注解的方法里加上下面这段代码来验证。

```java
logger.info("Current Thread : {}", Thread.currentThread().getName());
```

你会发现加上上面这段代码的定时任务，每次运行都会输出：

```
Current Thread : scheduling-1
```

如果我们需要自定义线程池执行话只需要新加一个实现`SchedulingConfigurer`接口的 `configureTasks` 的类即可，这个类需要加上 `@Configuration` 注解。

```java
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    private final int POOL_SIZE = 10;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
```

通过上面的验证的方式输出当前线程的名字会改变。

### 4. @EnableAsync 和 @Async  使定时任务并行执行

如果你想要你的代码并行执行的话，还可以通过`@EnableAsync` 和 @`Async `这两个注解实现

```java
@Component
@EnableAsync
public class AsyncScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(AsyncScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * fixedDelay：固定延迟执行。距离上一次调用成功后2秒才执。
     */
    //@Async
    @Scheduled(fixedDelay = 2000)
    public void reportCurrentTimeWithFixedDelay() {
        try {
            TimeUnit.SECONDS.sleep(3);
            log.info("Fixed Delay Task : The time is now {}", dateFormat.format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

运行程序输出如下，`reportCurrentTimeWithFixedDelay()`  方法会每5秒执行一次，因为我们说过了`@Scheduled`任务都在Spring创建的大小为1的默认线程池中执行。

```
Current Thread : scheduling-1
Fixed Delay Task : The time is now 14:24:23
Current Thread : scheduling-1
Fixed Delay Task : The time is now 14:24:28
Current Thread : scheduling-1
Fixed Delay Task : The time is now 14:24:33
```

`reportCurrentTimeWithFixedDelay()` 方法上加上 `@Async`   注解后输出如下，`reportCurrentTimeWithFixedDelay()`  方法会每 2 秒执行一次。

```
Current Thread : task-1
Fixed Delay Task : The time is now 14:27:32
Current Thread : task-2
Fixed Delay Task : The time is now 14:27:34
Current Thread : task-3
Fixed Delay Task : The time is now 14:27:36
```



代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/advanced/springboot-schedule-tast