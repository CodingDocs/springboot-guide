`@PostConstruct`和`@PreDestroy` 是两个作用于 Servlet 生命周期的注解，相信从 Servlet 开始学 Java 后台开发的同学对他应该不陌生。

**被这两个注解修饰的方法可以保证在整个 Servlet 生命周期只被执行一次，即使 Web 容器在其内部中多次实例化该方法所在的 bean。**

**这两个注解分别有什么作用呢**？

1. **`@PostConstruct`** : 用来修饰方法，标记在项目启动的时候执行这个方法,一般用来执行某些初始化操作比如全局配置。`PostConstruct` 注解的方法会在构造函数之后执行,Servlet 的`init()`方法之前执行。
2. **`@PreDestroy`** :  当 bean 被 Web 容器的时候被调用，一般用来释放 bean 所持有的资源。。`PostConstruct` 注解的方法会在Servlet 的`destroy()`方法之前执行。

被这个注解修饰的方法需要满足下面这些基本条件：

- 非静态
- 该方法必须没有任何参数，除非在拦截器的情况下，在这种情况下，它接受一个由拦截器规范定义的InvocationContext对象
- void()也就是没有返回值
- 该方法抛出未检查的异常
- ......

我们新建一个 Spring 程序，其中有一段代码是这样的，输出结果会是什么呢？

```java

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class MyConfiguration {
    public MyConfiguration() {
        System.out.println("构造方法被调用");
    }

    @PostConstruct
    private void init() {
        System.out.println("PostConstruct注解方法被调用");
    }

    @PreDestroy
    private void shutdown() {
        System.out.println("PreDestroy注解方法被调用");
    }
}


```

输出结果如下：

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-11/life-cycle-annotation01.jpg)

但是 J2EE已在Java 9中弃用 `@PostConstruct`和`@PreDestroy`这两个注解 ，并计划在Java 11中将其删除。我们有什么更好的替代方法吗？当然有！

```java
package cn.javaguide.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration2 implements InitializingBean, DisposableBean {
    public MyConfiguration2() {
        System.out.println("构造方法被调用");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet方法被调用");
    }

    @Override
    public void destroy() {
        System.out.println("destroy方法被调用");
    }

}

```

输出结果如下，可以看出实现Spring 提供的  `InitializingBean`和 `DisposableBean`接口的效果和使用`@PostConstruct`和`@PreDestroy` 注解的效果一样。

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-11/life-cycle-annotation02.jpg)

但是,Spring 官方不推荐使用上面这种方式，[Spring 官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factory-lifecycle)是这样说的：

> We recommend that you do not use the `InitializingBean` interface, because it unnecessarily couples the code to Spring. Alternatively, we suggest using the [`@PostConstruct`](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-postconstruct-and-predestroy-annotations) annotation or specifying a POJO initialization method. (我们建议您不要使用 `InitializingBean`回调接口，因为它不必要地将代码耦合到 Spring。另外，我们建议使用`@PostConstruct`注解或指定bean定义支持的通用方法。)

如果你还是非要使用 Java 9 及以后的版本使用 `@PostConstruct`和`@PreDestroy`  这两个注解的话，你也可以手动添加相关依赖。 

Maven:

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

Gradle:

```groovy
compile group: 'javax.annotation', name: 'javax.annotation-api', version: '1.3.2'
```

> 源码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/basis/life-cycle-annotation

推荐阅读：

- [Spring Bean Life Cycle](https://netjs.blogspot.com/2016/03/spring-bean-life-cycle.html)