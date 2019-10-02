### 1. 使用 **@ControllerAdvice和**@ExceptionHandler处理全局异常

这是目前很常用的一种方式，非常推荐。测试代码中用到了 Junit 5，如果你新建项目验证下面的代码的话，记得添加上相关依赖。

**1. 新建异常信息实体类**

非必要的类，主要用于包装异常信息。

`src/main/java/com/twuc/webApp/exception/ErrorResponse.java`

```java
/**
 * @author shuang.kou
 */
public class ErrorResponse {

    private String message;
    private String errorTypeName;
  
    public ErrorResponse(Exception e) {
        this(e.getClass().getName(), e.getMessage());
    }

    public ErrorResponse(String errorTypeName, String message) {
        this.errorTypeName = errorTypeName;
        this.message = message;
    }
    ......省略getter/setter方法
}
```

**2. 自定义异常类型**

`src/main/java/com/twuc/webApp/exception/ResourceNotFoundException.java`

一般我们处理的都是 `RuntimeException` ，所以如果你需要自定义异常类型的话直接集成这个类就可以了。

```java
/**
 * @author shuang.kou
 * 自定义异常类型
 */
public class ResourceNotFoundException extends RuntimeException {
    private String message;

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

**3. 新建异常处理类**

我们只需要在类上加上`@ControllerAdvice`注解这个类就成为了全局异常处理类，当然你也可以通过 `assignableTypes `指定特定的 `Controller `类，让异常处理类只处理特定类抛出的异常。

`src/main/java/com/twuc/webApp/exception/GlobalExceptionHandler.java`

```java
/**
 * @author shuang.kou
 */
@ControllerAdvice(assignableTypes = {ExceptionController.class})
@ResponseBody
public class GlobalExceptionHandler {

    ErrorResponse illegalArgumentResponse = new ErrorResponse(new IllegalArgumentException("参数错误!"));
    ErrorResponse resourseNotFoundResponse = new ErrorResponse(new ResourceNotFoundException("Sorry, the resourse not found!"));

    @ExceptionHandler(value = Exception.class)// 拦截所有异常, 这里只是为了演示，一般情况下一个方法特定处理一种异常
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {

        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(400).body(illegalArgumentResponse);
        } else if (e instanceof ResourceNotFoundException) {
            return ResponseEntity.status(404).body(resourseNotFoundResponse);
        }
        return null;
    }
}
```

**4. controller模拟抛出异常**

`src/main/java/com/twuc/webApp/web/ExceptionController.java`

```java
/**
 * @author shuang.kou
 */
@RestController
@RequestMapping("/api")
public class ExceptionController {

    @GetMapping("/illegalArgumentException")
    public void throwException() {
        throw new IllegalArgumentException();
    }

    @GetMapping("/resourceNotFoundException")
    public void throwException2() {
        throw new ResourceNotFoundException();
    }
}
```

使用  Get 请求 [localhost:8080/api/resourceNotFoundException](localhost:8333/api/resourceNotFoundException) （curl -i -s -X GET url），服务端返回的 JSON 数据如下：

```json
{
    "message": "Sorry, the resourse not found!",
    "errorTypeName": "com.twuc.webApp.exception.ResourceNotFoundException"
}
```

**5. 编写测试类** 

MockMvc 由`org.springframework.boot.test`包提供，实现了对Http请求的模拟，一般用于我们测试  controller 层。

```java
/**
 * @author shuang.kou
 */
@AutoConfigureMockMvc
@SpringBootTest
public class ExceptionTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void should_return_400_if_param_not_valid() throws Exception {
        mockMvc.perform(get("/api/illegalArgumentException"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("参数错误!"));
    }

    @Test
    void should_return_404_if_resourse_not_found() throws Exception {
        mockMvc.perform(get("/api/resourceNotFoundException"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message").value("Sorry, the resourse not found!"));
    }
}
```

### 2. @ExceptionHandler 处理 Controller 级别的异常

我们刚刚也说了使用`@ControllerAdvice`注解 可以通过 `assignableTypes `指定特定的类，让异常处理类只处理特定类抛出的异常。所以这种处理异常的方式，实际上现在使用的比较少了。

 我们把下面这段代码移到 `src/main/java/com/twuc/webApp/exception/GlobalExceptionHandler.java` 中就可以了。

```java
    @ExceptionHandler(value = Exception.class)// 拦截所有异常
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {

        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(400).body(illegalArgumentResponse);
        } else if (e instanceof ResourceNotFoundException) {
            return ResponseEntity.status(404).body(resourseNotFoundResponse);
        }
        return null;
    }
```

### 3. ResponseStatusException

研究 ResponseStatusException 我们先来看看，通过  `ResponseStatus`注解简单处理异常的方法（将异常映射为状态码）。

`src/main/java/com/twuc/webApp/exception/ResourceNotFoundException.java`

 ```java
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourseNotFoundException2 extends RuntimeException {

    public ResourseNotFoundException2() {
    }

    public ResourseNotFoundException2(String message) {
        super(message);
    }
}
 ```

 `src/main/java/com/twuc/webApp/web/ResponseStatusExceptionController.java`

```java
@RestController
@RequestMapping("/api")
public class ResponseStatusExceptionController {
    @GetMapping("/resourceNotFoundException2")
    public void throwException3() {
        throw new ResourseNotFoundException2("Sorry, the resourse not found!");
    }
}
```

  使用  Get 请求 [localhost:8080/api/resourceNotFoundException2](localhost:8333/api/resourceNotFoundException2) ，服务端返回的 JSON 数据如下：

```json
{
    "timestamp": "2019-08-21T07:11:43.744+0000",
    "status": 404,
    "error": "Not Found",
    "message": "Sorry, the resourse not found!",
    "path": "/api/resourceNotFoundException2"
}
```

这种通过 `ResponseStatus`注解简单处理异常的方法是的好处是比较简单，但是一般我们不会这样做，通过`ResponseStatusException`会更加方便,可以避免我们额外的异常类。

```java
    @GetMapping("/resourceNotFoundException2")
    public void throwException3() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, the resourse not found!", new ResourceNotFoundException());
    }
```

  使用  Get 请求 [localhost:8080/api/resourceNotFoundException2](localhost:8333/api/resourceNotFoundException2) ，服务端返回的 JSON 数据如下,和使用 `ResponseStatus`  实现的效果一样：

 ```json
{
    "timestamp": "2019-08-21T07:28:12.017+0000",
    "status": 404,
    "error": "Not Found",
    "message": "Sorry, the resourse not found!",
    "path": "/api/resourceNotFoundException3"
}
 ```

`ResponseStatusException` 提供了三个构造方法：

```java
	public ResponseStatusException(HttpStatus status) {
		this(status, null, null);
	}

	public ResponseStatusException(HttpStatus status, @Nullable String reason) {
		this(status, reason, null);
	}

	public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
		super(null, cause);
		Assert.notNull(status, "HttpStatus is required");
		this.status = status;
		this.reason = reason;
	}

```

构造函数中的参数解释如下：

- status ： http status
- reason ：response 的消息内容
- cause ： 抛出的异常

相关代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/springboot-handle-exception