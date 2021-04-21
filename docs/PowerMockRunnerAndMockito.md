单元测试可以提高测试开发的效率，减少代码错误率，提高代码健壮性，提高代码质量。在 Spring 框架中常用的两种测试框架：`PowerMockRunner` 和 `SpringRunner` 两个单元测试，鉴于 `SpringRunner` 启动的一系列依赖和数据连接的问题，推荐使用 `PowerMockRunner`，这样能有效的提高测试的效率，并且其提供的 API 能覆盖的场景广泛，使用方便，可谓是 Java 单元测试之模拟利器。

## 1. PowerMock 是什么？

`PowerMock` 是一个 Java 模拟框架，可用于解决通常认为很难甚至无法测试的测试问题。使用 `PowerMock`，可以模拟静态方法，删除静态初始化程序，允许模拟而不依赖于注入，等等。`PowerMock` 通过在执行测试时在运行时修改字节码来完成这些技巧。`PowerMock` 还包含一些实用程序，可让您更轻松地访问对象的内部状态。

举个例子，你在使用 `Junit` 进行单元测试时，并不想让测试数据进入数据库，怎么办？这个时候就可以使用 `PowerMock`，拦截数据库操作，并模拟返回参数。

## 2. PowerMock 包引入

```xml
<!-- 单元测试 依赖-->
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-core</artifactId>
    <version>2.0.2</version>
    <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>2.23.0</version>
</dependency>
<dependency>
  <groupId>org.powermock</groupId>
  <artifactId>powermock-module-junit4</artifactId>
  <version>2.0.4</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.powermock</groupId>
  <artifactId>powermock-api-mockito2</artifactId>
  <version>2.0.2</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>com.github.jsonzou</groupId>
  <artifactId>jmockdata</artifactId>
  <version>4.3.0</version>
</dependency>
```

## 3. 重要注解说明

```java
@RunWith(PowerMockRunner.class) // 告诉JUnit使用PowerMockRunner进行测试
@PrepareForTest({RandomUtil.class}) // 所有需要测试的类列在此处，适用于模拟final类或有final, private, static, native方法的类
@PowerMockIgnore("javax.management.*") //为了解决使用powermock后，提示classloader错误
```

## 4. 使用示例

### 4.1 模拟接口返回

首先对接口进行 mock，然后录制相关行为

```java
InterfaceToMock mock = Powermockito.mock(InterfaceToMock.class)
Powermockito.when(mock.method(Params…)).thenReturn(value)
Powermockito.when(mock.method(Params..)).thenThrow(Exception)
```

### 4.2 设置对象的 private 属性

需要使用 `Whitebox` 向 class 或者对象中赋值。

如我们已经对接口尽心了 mock，现在需要将此 mock 加入到对象中，可以采用如下方法：

```java
Whitebox.setInternalState(Object object, String fieldname, Object… value);
```

其中 object 为需要设置属性的静态类或对象。

### 4.3 模拟构造函数

对于模拟构造函数，也即当出现 `new InstanceClass()` 时可以将此构造函数拦截并替换结果为我们需要的 mock 对象。

注意：使用时需要加入标记：

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest({ InstanceClass.class })
@PowerMockIgnore("javax.management.\*")

Powermockito.whenNew(InstanceClass.class).thenReturn(Object value)
```

##### 4.4 模拟静态方法

模拟静态方法类似于模拟构造函数，也需要加入注释标记。

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest({ StaticClassToMock.class })
@PowerMockIgnore("javax.management.\*")

Powermockito.mockStatic(StaticClassToMock.class);
Powermockito.when(StaticClassToMock.method(Object.. params)).thenReturn(Object value)
```

##### 4.5 模拟 final 方法

Final 方法的模拟类似于模拟静态方法。

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FinalClassToMock.class })
@PowerMockIgnore("javax.management.\*")

Powermockito.mockStatic(FinalClassToMock.class);
Powermockito.when(StaticClassToMock.method(Object.. params)).thenReturn(Object value)
```

### 4.6 模拟静态类

模拟静态类类似于模拟静态方法。

### 4.7 使用 spy 方法避免执行被测类中的成员函数

如被测试类为：TargetClass，想要屏蔽的方法为 targetMethod.

```java
1） PowerMockito.spy(TargetClass.class);

2） Powemockito.when(TargetClass.targetMethod()).doReturn()

3） 注意加入

@RunWith(PowerMockRunner.class)
@PrepareForTest(DisplayMoRelationBuilder.class)
@PowerMockIgnore("javax.management.*")
```

### 4.8 参数匹配器

有时我们在处理 `doMethod(Param param)` 时，不想进行精确匹配，这时可以使用 `Mockito` 提供的模糊匹配方式。

如：`Mockito.anyInt()`，`Mockito.anyString()`

### 4.9 处理 public void 型的静态方法

```java
Powermockito.doNothing.when(T class2mock, String method, <T>… params>
```

## 5. 单元测试用例可选清单

输入数据验证：这些检查通常可以对输入到应用程序系统中的数据采用。

- 必传项测试
- 唯一字段值测试
- 空值测试
- 字段只接受允许的字符
- 负值测试
- 字段限于字段长度规范
- 不可能的值
- 垃圾值测试
- 检查字段之间的依赖性
- 等效类划分和边界条件测试
- 错误和异常处理测试单元测试可以提高测试开发的效率，减少代码错误率，提高代码健壮性，提高代码质量。在 Spring 框架中常用的两种测试框架：PowerMockRunner 和 SpringRunner 两个单元测试，鉴于 SpringRunner 启动的一系列依赖和数据连接的问题，推荐使用 PowerMockRunner，这样能有效的提高测试的效率，并且其提供的 API 能覆盖的场景广泛，使用方便，可谓是 Java 单元测试之模拟利器。