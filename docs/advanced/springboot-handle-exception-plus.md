在上一篇文章中我介绍了：

1. 使用 `@ControllerAdvice` 和 `@ExceptionHandler` 处理全局异常
2. `@ExceptionHandler` 处理 Controller 级别的异常
3. `ResponseStatusException` 

通过这篇文章，可以搞懂如何在 Spring Boot 中进行异常处理。但是，光是会用了还不行，我们还要思考如何把异常处理这部分的代码写的稍微优雅一点。下面我会以我在工作中学到的一点实际项目中异常处理的方式，来说说我觉得稍微优雅点的异常处理解决方案。

下面仅仅是我作为一个我个人的角度来看的，如果各位读者有更好的解决方案或者觉得本文提出的方案还有优化的余地的话，欢迎在评论区评论。

## 最终效果展示

下面先来展示一下完成后的效果，当我们定义的异常被系统捕捉后返回给客户端的信息是这样的：

![效果展示](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-11/异常处理plus-效果展示.jpg)

返回的信息包含了异常下面 5 部分内容：

1. 唯一标示异常的 code
2. HTTP状态码
3. 错误路径
4. 发生错误的时间戳
5. 错误的具体信息

这样返回异常信息，更利于我们前端根据异常信息做出相应的表现。

## 异常处理核心代码

`ErrorCode.java` (此枚举类中包含了异常的唯一标识、HTTP状态码以及错误信息)

这个类的主要作用就是统一管理系统中可能出现的异常，比较清晰明了。但是，可能出现的问题是当系统过于复杂，出现的异常过多之后，这个类会比较庞大。有一种解决办法：将多种相似的异常统一为一个，比如将用户找不到异常和订单信息未找到的异常都统一为“未找到该资源”这一种异常，然后前端再对相应的情况做详细处理（我个人的一种处理方法，不敢保证是比较好的一种做法）。

```java
import org.springframework.http.HttpStatus;


public enum ErrorCode {
    RESOURCE_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "未找到该资源"),
    REQUEST_VALIDATION_FAILED(1002, HttpStatus.BAD_REQUEST, "请求数据格式验证失败");
    private final int code;

    private final HttpStatus status;

    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
```

**`ErrorReponse.java`（返回给客户端具体的异常对象）**

```java
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ErrorReponse {
    private int code;
    private int status;
    private String message;
    private String path;
    private Instant timestamp;
    private HashMap<String, Object> data = new HashMap<String, Object>();

    public ErrorReponse() {
    }

    public ErrorReponse(BaseException ex, String path) {
        this(ex.getError().getCode(), ex.getError().getStatus().value(), ex.getError().getMessage(), path, ex.getData());
    }

    public ErrorReponse(int code, int status, String message, String path, Map<String, Object> data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

// 省略 getter/setter 方法

    @Override
    public String toString() {
        return "ErrorReponse{" +
                "code=" + code +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}

```

**`BaseException.java`（继承自 `RuntimeException` 的抽象类，可以看做系统中其他异常类的父类）**

```java
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shuang.kou
 */
public abstract class BaseException extends RuntimeException {
    private final ErrorCode error;
    private final HashMap<String, Object> data = new HashMap<>();

    public BaseException(ErrorCode error, Map<String, Object> data) {
        super(format(error.getCode(), error.getMessage(), data));
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    protected BaseException(ErrorCode error, Map<String, Object> data, Throwable cause) {
        super(format(error.getCode(), error.getMessage(), data), cause);
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    private static String format(Integer code, String message, Map<String, Object> data) {
        return String.format("[%d]%s:%s.", code, message, ObjectUtils.isEmpty(data) ? "" : data.toString());
    }

    public ErrorCode getError() {
        return error;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
```

**`ResourceNotFoundException.java` （自定义异常）**

可以看出通过继承 `BaseException` 类我们自定义异常会变的非常简单！

```java
import java.util.Map;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(Map<String, Object> data) {
        super(ErrorCode.RESOURCE_NOT_FOUND, data);
    }
}
```

**`GlobalExceptionHandler.java`（全局异常捕获）**

`@ExceptionHandler` 捕获异常的过程中，会优先找到最匹配的。

```java
/**
 * @author shuang.kou
 */
@ControllerAdvice(assignableTypes = {ExceptionController.class})
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleAppException(BaseException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorReponse representation = new ErrorReponse(ex, path);
        return new ResponseEntity<>(representation, new HttpHeaders(), ex.getError().getStatus());
    }


    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorReponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorReponse errorReponse = new ErrorReponse(ex, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorReponse);
    }
}

```

## 写一个抛出异常的类测试

**`Person.java`**

```java
public class Person {
    private Long id;
    private String name;

    // 省略 getter/setter 方法
}
```

**`ExceptionController.java`（抛出一场的类）**

```java
@RestController
@RequestMapping("/api")
public class ExceptionController {

    @GetMapping("/resourceNotFound")
    public void throwException() {
        Person p=new Person(1L,"SnailClimb");
        throw new ResourceNotFoundException(ImmutableMap.of("person id:", p.getId()));
    }

}
```

