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

这个类作为异常信息返回给客户端，里面包括了当出现异常时我们想要返回给客户端的所有信息。

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

系统中的异常类都要继承自这个类。

```java

public abstract class BaseException extends RuntimeException {
    private final ErrorCode error;
    private final HashMap<String, Object> data = new HashMap<>();

    public BaseException(ErrorCode error, Map<String, Object> data) {
        super(error.getMessage());
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    protected BaseException(ErrorCode error, Map<String, Object> data, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
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

我们定义了两个异常捕获方法。

这里再说明一下，实际上这个类只需要 `handleAppException()` 这一个方法就够了，因为它是本系统所有异常的父类。只要是抛出了继承 `BaseException` 类的异常后都会在这里被处理。

```java
import com.twuc.webApp.web.ExceptionController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = {ExceptionController.class})
@ResponseBody
public class GlobalExceptionHandler {
    
    // 也可以将 BaseException 换为 RuntimeException 
    // 因为 RuntimeException 是 BaseException 的父类
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleAppException(BaseException ex, HttpServletRequest request) {
        ErrorReponse representation = new ErrorReponse(ex, request.getRequestURI());
        return new ResponseEntity<>(representation, new HttpHeaders(), ex.getError().getStatus());
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorReponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorReponse errorReponse = new ErrorReponse(ex, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorReponse);
    }
}

```

**（重要）一点扩展：**

哈哈！实际上我多加了一个算是多余的异常捕获方法`handleResourceNotFoundException()` 主要是为了考考大家当我们抛出了 `ResourceNotFoundException`异常会被下面哪一个方法捕获呢？

答案：

会被`handleResourceNotFoundException()`方法捕获。因为 `@ExceptionHandler` 捕获异常的过程中，会优先找到最匹配的。

下面通过源码简单分析一下：

`ExceptionHandlerMethodResolver.java`中`getMappedMethod`决定了具体被哪个方法处理。

```java

@Nullable
	private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
		List<Class<? extends Throwable>> matches = new ArrayList<>();
    //找到可以处理的所有异常信息。mappedMethods 中存放了异常和处理异常的方法的对应关系
		for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
			if (mappedException.isAssignableFrom(exceptionType)) {
				matches.add(mappedException);
			}
		}
    // 不为空说明有方法处理异常
		if (!matches.isEmpty()) {
      // 按照匹配程度从小到大排序
			matches.sort(new ExceptionDepthComparator(exceptionType));
      // 返回处理异常的方法
			return this.mappedMethods.get(matches.get(0));
		}
		else {
			return null;
		}
	}
```

从源代码看出：**`getMappedMethod()`会首先找到可以匹配处理异常的所有方法信息，然后对其进行从小到大的排序，最后取最小的那一个匹配的方法(即匹配度最高的那个)。**

## 写一个抛出异常的类测试

**`Person.java`**

```java
public class Person {
    private Long id;
    private String name;

    // 省略 getter/setter 方法
}
```

**`ExceptionController.java`（抛出异常的类）**

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

源码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/basis/springboot-handle-exception-improved

