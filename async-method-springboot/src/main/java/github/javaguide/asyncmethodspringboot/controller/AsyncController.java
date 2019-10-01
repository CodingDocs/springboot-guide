package github.javaguide.asyncmethodspringboot.controller;

import github.javaguide.asyncmethodspringboot.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/** @author shuang.kou */
@RestController
@RequestMapping("/async")
public class AsyncController {
  @Autowired
  AsyncService asyncService;

  @GetMapping("/movies")
  public String completableFutureTask() throws ExecutionException, InterruptedException {
    //开始时间
    long start = System.currentTimeMillis();
    // 开始执行大量的异步任务
    List<String> words = Arrays.asList("F", "T", "S", "Z", "J", "C");
    List<CompletableFuture<List<String>>> completableFutureList =
            words.stream()
                    .map(word -> asyncService.completableFutureTask(word))
                    .collect(Collectors.toList());
    // CompletableFuture.join（）方法可以获取他们的结果并将结果连接起来
    List<List<String>> results = completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
    // 打印结果以及运行程序运行花费时间
    System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
    return results.toString();
  }
}
