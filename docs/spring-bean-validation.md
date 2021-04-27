数据的校验的重要性就不用说了，即使在前端对数据进行校验的情况下，我们还是要对传入后端的数据再进行一遍校验，避免用户绕过浏览器直接通过一些 HTTP 工具直接向后端请求一些违法数据。

最普通的做法就像下面这样。我们通过 `if/else` 语句对请求的每一个参数一一校验。

```java
@RestController
@RequestMapping("/api/person")
public class PersonController {

    @PostMapping
    public ResponseEntity<PersonRequest> save(@RequestBody PersonRequest personRequest) {
        if (personRequest.getClassId() == null
                || personRequest.getName() == null
                || !Pattern.matches("(^Man$|^Woman$|^UGM$)", personRequest.getSex())) {

        }
        return ResponseEntity.ok().body(personRequest);
    }
}
```

这样的代码，小伙伴们在日常开发中一定不少见，很多开源项目都是这样对请求入参做校验的。

但是，不太建议这样来写，这样的代码明显违背了 **单一职责原则**。大量的非业务代码混杂在业务代码中，非常难以维护，还会导致业务层代码冗杂！

实际上，我们是可以通过一些简单的手段对上面的代码进行改进的！这也是本文主要要介绍的内容！

废话不多说！下面我会结合自己在项目中的实际使用经验，通过实例程序演示如何在 SpringBoot 程序中优雅地的进行参数验证(普通的 Java 程序同样适用)。

不了解的朋友一定要好好看一下，学完马上就可以实践到项目上去。

并且，本文示例项目使用的是目前最新的 Spring Boot 版本 2.4.5!（截止到 2021-04-21）

示例项目源代码地址：[https://github.com/CodingDocs/springboot-guide/tree/master/source-code/bean-validation-demo](https://github.com/CodingDocs/springboot-guide/tree/master/source-code/bean-validation-demo) 。

## 添加相关依赖

如果开发普通 Java 程序的的话，你需要可能需要像下面这样依赖：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.9.Final</version>
</dependency>
<dependency>
    <groupId>javax.el</groupId>
    <artifactId>javax.el-api</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>javax.el</artifactId>
    <version>2.2.6</version>
</dependency>
```

不过，相信大家都是使用的 Spring Boot 框架来做开发。

基于 Spring Boot 的话，就比较简单了，只需要给项目添加上 `spring-boot-starter-web` 依赖就够了，它的子依赖包含了我们所需要的东西。另外，我们的示例项目中还使用到了 Lombok。

![](https://img-blog.csdnimg.cn/20210421172058450.png)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

但是！！！ Spring Boot 2.3 1 之后，`spring-boot-starter-validation` 已经不包括在了 `spring-boot-starter-web` 中，需要我们手动加上！

![](https://img-blog.csdnimg.cn/20210421170846695.png)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 验证 Controller 的输入

### 验证请求体

验证请求体即使验证被 `@RequestBody` 注解标记的方法参数。

**`PersonController`**

我们在需要验证的参数上加上了`@Valid`注解，如果验证失败，它将抛出`MethodArgumentNotValidException`。默认情况下，Spring 会将此异常转换为 HTTP Status 400（错误请求）。

```java
@RestController
@RequestMapping("/api/person")
@Validated
public class PersonController {

    @PostMapping
    public ResponseEntity<PersonRequest> save(@RequestBody @Valid PersonRequest personRequest) {
        return ResponseEntity.ok().body(personRequest);
    }
}
```

**`PersonRequest`**

我们使用校验注解对请求的参数进行校验！

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest {

    @NotNull(message = "classId 不能为空")
    private String classId;

    @Size(max = 33)
    @NotNull(message = "name 不能为空")
    private String name;

    @Pattern(regexp = "(^Man$|^Woman$|^UGM$)", message = "sex 值不在可选范围")
    @NotNull(message = "sex 不能为空")
    private String sex;

}

```

正则表达式说明：

- `^string` : 匹配以 string 开头的字符串
- `string$` ：匹配以 string 结尾的字符串
- `^string$` ：精确匹配 string 字符串
- `(^Man$|^Woman$|^UGM$)` : 值只能在 Man,Woman,UGM 这三个值中选择

**`GlobalExceptionHandler`**

自定义异常处理器可以帮助我们捕获异常，并进行一些简单的处理。如果对于下面的处理异常的代码不太理解的话，可以查看这篇文章 [《SpringBoot 处理异常的几种常见姿势》](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247485568&idx=2&sn=c5ba880fd0c5d82e39531fa42cb036ac&chksm=cea2474bf9d5ce5dcbc6a5f6580198fdce4bc92ef577579183a729cb5d1430e4994720d59b34&token=1924773784&lang=zh_CN#rd)。

```java
@ControllerAdvice(assignableTypes = {PersonController.class})
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

**通过测试验证**

下面我通过 `MockMvc` 模拟请求 `Controller` 的方式来验证是否生效。当然了，你也可以通过 `Postman` 这种工具来验证。

```java
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 验证出现参数不合法的情况抛出异常并且可以正确被捕获
     */
    @Test
    public void should_check_person_value() throws Exception {
        PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                .classId("82938390").build();
        mockMvc.perform(post("/api/personRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(MockMvcResultMatchers.jsonPath("sex").value("sex 值不在可选范围"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("name 不能为空"));
    }
}
```

**使用 `Postman` 验证**

![](https://img-blog.csdnimg.cn/20210421175345253.png)

### 验证请求参数

验证请求参数（Path Variables 和 Request Parameters）即是验证被 `@PathVariable` 以及 `@RequestParam` 标记的方法参数。

**`PersonController`**

**一定一定不要忘记在类上加上 `Validated` 注解了，这个参数可以告诉 Spring 去校验方法参数。**

```java
@RestController
@RequestMapping("/api/persons")
@Validated
public class PersonController {

    @GetMapping("/{id}")
    public ResponseEntity<Integer> getPersonByID(@Valid @PathVariable("id") @Max(value = 5, message = "超过 id 的范围了") Integer id) {
        return ResponseEntity.ok().body(id);
    }

    @PutMapping
    public ResponseEntity<String> getPersonByName(@Valid @RequestParam("name") @Size(max = 6, message = "超过 name 的范围了") String name) {
        return ResponseEntity.ok().body(name);
    }
}
```

**`ExceptionHandler`**

```java
  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }
```

**通过测试验证**

```java
@Test
public void should_check_path_variable() throws Exception {
    mockMvc.perform(get("/api/person/6")
                    .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(content().string("getPersonByID.id: 超过 id 的范围了"));
}

@Test
public void should_check_request_param_value2() throws Exception {
    mockMvc.perform(put("/api/person")
                    .param("name", "snailclimbsnailclimb")
                    .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(content().string("getPersonByName.name: 超过 name 的范围了"));
}
```

**使用 `Postman` 验证**

![](https://img-blog.csdnimg.cn/20210421190508416.png)

![](https://img-blog.csdnimg.cn/20210421190810975.png)

## 验证 Service 中的方法

我们还可以验证任何 Spring Bean 的输入，而不仅仅是 `Controller` 级别的输入。通过使用`@Validated`和`@Valid`注释的组合即可实现这一需求！

一般情况下，我们在项目中也更倾向于使用这种方案。

**一定一定不要忘记在类上加上 `Validated` 注解了，这个参数可以告诉 Spring 去校验方法参数。**

```java
@Service
@Validated
public class PersonService {

    public void validatePersonRequest(@Valid PersonRequest personRequest) {
        // do something
    }

}
```

**通过测试验证：**

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceTest {
    @Autowired
    private PersonService service;

    @Test
    public void should_throw_exception_when_person_request_is_not_valid() {
        try {
            PersonRequest personRequest = PersonRequest.builder().sex("Man22")
                    .classId("82938390").build();
            service.validatePersonRequest(personRequest);
        } catch (ConstraintViolationException e) {
           // 输出异常信息
            e.getConstraintViolations().forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
        }
    }
}
```

输出结果如下：

```
name 不能为空
sex 值不在可选范围
```

## Validator 编程方式手动进行参数验证

某些场景下可能会需要我们手动校验并获得校验结果。

我们通过 `Validator` 工厂类获得的 `Validator` 示例。另外，如果是在 Spring Bean 中的话，还可以通过 `@Autowired` 直接注入的方式。

```java
@Autowired
Validator validate
```

具体使用情况如下：

```java
/**
 * 手动校验对象
 */
@Test
public void check_person_manually() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    PersonRequest personRequest = PersonRequest.builder().sex("Man22")
            .classId("82938390").build();
    Set<ConstraintViolation<PersonRequest>> violations = validator.validate(personRequest);
    violations.forEach(constraintViolation -> System.out.println(constraintViolation.getMessage()));
}
```

输出结果如下：

```
sex 值不在可选范围
name 不能为空
```

## 自定义 Validator(实用)

如果自带的校验注解无法满足你的需求的话，你还可以自定义实现注解。

### 案例一:校验特定字段的值是否在可选范围

比如我们现在多了这样一个需求：`PersonRequest` 类多了一个 `Region` 字段，`Region` 字段只能是`China`、`China-Taiwan`、`China-HongKong`这三个中的一个。

**第一步，你需要创建一个注解 `Region`。**

```java
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = RegionValidator.class)
@Documented
public @interface Region {

    String message() default "Region 值不在可选范围内";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

**第二步，你需要实现 `ConstraintValidator`接口，并重写`isValid` 方法。**

```java
public class RegionValidator implements ConstraintValidator<Region, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        HashSet<Object> regions = new HashSet<>();
        regions.add("China");
        regions.add("China-Taiwan");
        regions.add("China-HongKong");
        return regions.contains(value);
    }
}

```

现在你就可以使用这个注解：

```java
@Region
private String region;
```

**通过测试验证**

```java
PersonRequest personRequest = PersonRequest.builder()
 	 .region("Shanghai").build();
mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personRequest)))
  .andExpect(MockMvcResultMatchers.jsonPath("region").value("Region 值不在可选范围内"));
```

**使用 `Postman` 验证**

![](https://img-blog.csdnimg.cn/20210421203330978.png)

### 案例二:校验电话号码

校验我们的电话号码是否合法，这个可以通过正则表达式来做，相关的正则表达式都可以在网上搜到，你甚至可以搜索到针对特定运营商电话号码段的正则表达式。

`PhoneNumber.java`

```java
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class[] groups() default {};
    Class[] payload() default {};
}
```

`PhoneNumberValidator.java`

```java
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        if (phoneField == null) {
            // can be null
            return true;
        }
        //  大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^[1]((3[0-9])|(4[5-9])|(5[0-3,5-9])|([6][5,6])|(7[0-9])|(8[0-9])|(9[1,8,9]))\\d{8}$";
        return phoneField.matches(regExp);
    }
}

```

搞定，我们现在就可以使用这个注解了。

```java
@PhoneNumber(message = "phoneNumber 格式不正确")
@NotNull(message = "phoneNumber 不能为空")
private String phoneNumber;
```

**通过测试验证**

```java
PersonRequest personRequest = PersonRequest.builder()
  	.phoneNumber("1816313815").build();
mockMvc.perform(post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personRequest)))
  .andExpect(MockMvcResultMatchers.jsonPath("phoneNumber").value("phoneNumber 格式不正确"));
```

![](https://img-blog.csdnimg.cn/20210421204116640.png)

## 使用验证组

验证组我们基本是不会用到的，也不太建议在项目中使用，理解起来比较麻烦，写起来也比较麻烦。简单了解即可！

当我们对对象操作的不同方法有不同的验证规则的时候才会用到验证组。

我写一个简单的例子，你们就能看明白了！

**1.先创建两个接口，代表不同的验证组**

```java
public interface AddPersonGroup {
}
public interface DeletePersonGroup {
}
```

**2.使用验证组**

```java
@Data
public class Person {
    // 当验证组为 DeletePersonGroup 的时候 group 字段不能为空
    @NotNull(groups = DeletePersonGroup.class)
    // 当验证组为 AddPersonGroup 的时候 group 字段需要为空
    @Null(groups = AddPersonGroup.class)
    private String group;
}

@Service
@Validated
public class PersonService {

    @Validated(AddPersonGroup.class)
    public void validatePersonGroupForAdd(@Valid Person person) {
        // do something
    }

    @Validated(DeletePersonGroup.class)
    public void validatePersonGroupForDelete(@Valid Person person) {
        // do something
    }

}
```

通过测试验证：

```java
  @Test(expected = ConstraintViolationException.class)
  public void should_check_person_with_groups() {
      Person person = new Person();
      person.setGroup("group1");
      service.validatePersonGroupForAdd(person);
  }

  @Test(expected = ConstraintViolationException.class)
  public void should_check_person_with_groups2() {
      Person person = new Person();
      service.validatePersonGroupForDelete(person);
  }
```

验证组使用下来的体验就是有点反模式的感觉，让代码的可维护性变差了！尽量不要使用！

## 常用校验注解总结

`JSR303` 定义了 `Bean Validation`（校验）的标准 `validation-api`，并没有提供实现。`Hibernate Validation`是对这个规范/规范的实现 `hibernate-validator`，并且增加了 `@Email`、`@Length`、`@Range` 等注解。`Spring Validation` 底层依赖的就是`Hibernate Validation`。

**JSR 提供的校验注解**:

- `@Null` 被注释的元素必须为 null
- `@NotNull` 被注释的元素必须不为 null
- `@AssertTrue` 被注释的元素必须为 true
- `@AssertFalse` 被注释的元素必须为 false
- `@Min(value)` 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
- `@Max(value)` 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
- `@DecimalMin(value)` 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
- `@DecimalMax(value)` 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
- `@Size(max=, min=)` 被注释的元素的大小必须在指定的范围内
- `@Digits (integer, fraction)` 被注释的元素必须是一个数字，其值必须在可接受的范围内
- `@Past` 被注释的元素必须是一个过去的日期
- `@Future` 被注释的元素必须是一个将来的日期
- `@Pattern(regex=,flag=)` 被注释的元素必须符合指定的正则表达式

**Hibernate Validator 提供的校验注解**：

- `@NotBlank(message =)` 验证字符串非 null，且长度必须大于 0
- `@Email` 被注释的元素必须是电子邮箱地址
- `@Length(min=,max=)` 被注释的字符串的大小必须在指定的范围内
- `@NotEmpty` 被注释的字符串的必须非空
- `@Range(min=,max=,message=)` 被注释的元素必须在合适的范围内

## 拓展

经常有小伙伴问到：“`@NotNull` 和 `@Column(nullable = false)` 两者有什么区别？”

我这里简单回答一下：

- `@NotNull`是 JSR 303 Bean 验证批注,它与数据库约束本身无关。
- `@Column(nullable = false)` : 是 JPA 声明列为非空的方法。

总结来说就是即前者用于验证，而后者则用于指示数据库创建表的时候对表的约束。
