package github.javaguide.asyncmethodspringboot.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncServiceTest {

  @Autowired AsyncService asyncService;

  @Test
  public void testCompletableFutureTask() throws InterruptedException, ExecutionException {
    // 开始时间
    long start = System.currentTimeMillis();
    // 开始执行大量的异步任务
    List<String> words = Arrays.asList("F", "T", "S", "Z", "J", "C");
    List<CompletableFuture<List<String>>> completableFutureList =
        words.stream()
            .map(word -> asyncService.completableFutureTask(word))
            .collect(Collectors.toList());
    // CompletableFuture.join（）方法可以获取他们的结果并将结果连接起来
    List<List<String>> results =
        completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
    // 打印结果以及运行程序运行花费时间
    System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
    System.out.println(results.toString());
  }
}
