本节我们将开发一个简单的 RESTful Web 服务。

 RESTful Web 服务与传统的 MVC 开发一个关键区别是返回给客户端的内容的创建方式：**传统的 MVC 模式开发会直接返回给客户端一个视图，但是 RESTful Web 服务一般会将返回的数据以 JSON 的形式返回，这也就是现在所推崇的前后端分离开发。**

为了节省时间，本篇内容的代码是在 **[Spring Boot 版 Hello World & Spring Boot 项目结构分析](https://snailclimb.gitee.io/springboot-guide/#/./start/springboot-hello-world)** 基础上进行开发的。

通过下面的内容你将学习到下面这些东西：

1. Lombok 优化代码利器
2. `@RestController`
3. `@RequestParam`以及`@Pathvairable`
4. `@RequestMapping`、` @GetMapping`......

因为本次开发用到了 Lombok 这个简化 Java 代码的工具，所以我们需要在 pom.xml 中添加相关依赖。

```xml
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.10</version>
		</dependency>
```

并且你需要下载 IDEA 中支持 lombok 的插件：

![ IDEA 中下载支持 lombok 的插件](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/lombok-idea.png)

假如我们有一个书架，上面放了很多书。为此，我们需要新建一个 `Book` 实体类。

`com.example.helloworld.entity`

```java
/**
 * @author shuang.kou
 */
@Data
public class Book {
    private String name;
    private String description;
}
```

我们还需要一个控制器对书架上进行添加、查找以及查看。为此，我们需要新建一个 `BookController` 。

```java
import com.example.helloworld.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BookController {

    private List<Book> books = new ArrayList<>();

    @PostMapping("/book")
    public ResponseEntity<List<Book>> addBook(@RequestBody Book book) {
        books.add(book);
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/book/{id}")
    public ResponseEntity deleteBookById(@PathVariable("id") int id) {
        books.remove(id);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/book")
    public ResponseEntity getBookByName(@RequestParam("name") String name) {
        List<Book> results = books.stream().filter(book -> book.getName().equals(name)).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}
```

1. `@RestController`  将返回的对象数据直接以 JSON 或 XML 形式写入 HTTP 响应(Response)中。绝大部分情况下都是直接以  JSON 形式返回给客户端，很少的情况下才会以 XML 形式返回。转换成 XML 形式还需要额为的工作，上面代码中演示的直接就是将对象数据直接以 JSON 形式写入 HTTP 响应(Response)中。
2. `@RequestMapping` :上面的示例中没有指定 GET 与 PUT、POST 等，因为@RequestMapping默认映射所有HTTP Action，你可以使用`@RequestMapping(method=ActionType)`来缩小这个映射。
3. ` @PostMapping`实际上就等价于 `@RequestMapping(method = RequestMethod.POST)`，同样的 ` @DeleteMapping` ,`@GetMapping`也都一样，常用的 HTTP Action 都有一个这种形式的注解所对应。
4. `@PathVariable` :取url地址中的参数。`@RequestParam("name") ` url的查询参数值。



