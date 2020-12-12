## 1.SpringBoot 介绍

### 1.1 先从 Spring 谈起

Spring 是重量级企业开发框架 **Enterprise JavaBean（EJB）** 的替代品，Spring 为企业级 Java 开发提供了一种相对简单的方法，通过 **依赖注入** 和 **面向切面编程** ，用简单的 **Java 对象（Plain Old Java Object，POJO）** 实现了 EJB 的功能

**虽然 Spring 的组件代码是轻量级的，但它的配置却是重量级的（需要大量 XML 配置）** 。

为此，Spring 2.5 引入了基于注解的组件扫描，这消除了大量针对应用程序自身组件的显式 XML 配置。Spring 3.0 引入了基于 Java 的配置，这是一种类型安全的可重构配置方式，可以代替 XML。

尽管如此，我们依旧没能逃脱配置的魔爪。开启某些 Spring 特性时，比如事务管理和 Spring MVC，还是需要用 XML 或 Java 进行显式配置。启用第三方库时也需要显式配置，比如基于 Thymeleaf 的 Web 视图。配置 Servlet 和过滤器（比如 Spring 的`DispatcherServlet`）同样需要在 web.xml 或 Servlet 初始化代码里进行显式配置。组件扫描减少了配置量，Java 配置让它看上去简洁不少，但 Spring 还是需要不少配置。

光配置这些 XML 文件都够我们头疼的了，占用了我们大部分时间和精力。除此之外，相关库的依赖非常让人头疼，不同库之间的版本冲突也非常常见。

**不过，好消息是：Spring Boot 让这一切成为了过去。**

### 1.2 再来谈谈 Spring Boot

**最好直白的介绍莫过于官方的介绍：**

> Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can “just run”...Most Spring Boot applications need very little Spring configuration.(Spring Boot 可以轻松创建独立的生产级基于 Spring 的应用程序,只要通过 “just run”（可能是 run ‘Application’或 java -jar 或 tomcat 或 maven 插件 run 或 shell 脚本）便可以运行项目。大部分 Spring Boot 项目只需要少量的配置即可)

**简而言之，从本质上来说，Spring Boot 就是 Spring，它做了那些没有它你自己也会去做的 Spring Bean 配置。**

#### 1.2.1 为什么需要 Spring Boot?

Spring Framework 旨在简化 J2EE 企业应用程序开发。Spring Boot Framework 旨在简化 Spring 开发。

![why-we-need-springboot](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/why-we-need-springboot.png)

#### 1.2.2 Spring Boot 的主要优点

1. 开发基于 Spring 的应用程序很容易。
2. Spring Boot 项目所需的开发或工程时间明显减少，通常会提高整体生产力。
3. Spring Boot 不需要编写大量样板代码、XML 配置和注释。
4. Spring 引导应用程序可以很容易地与 Spring 生态系统集成，如 Spring JDBC、Spring ORM、Spring Data、Spring Security 等。
5. Spring Boot 遵循“固执己见的默认配置”，以减少开发工作（默认配置可以修改）。
6. Spring Boot 应用程序提供嵌入式 HTTP 服务器，如 Tomcat 和 Jetty，可以轻松地开发和测试 web 应用程序。（这点很赞！普通运行 Java 程序的方式就能运行基于 Spring Boot web 项目，省事很多）
7. Spring Boot 提供命令行接口(CLI)工具，用于开发和测试 Spring Boot 应用程序，如 Java 或 Groovy。
8. Spring Boot 提供了多种插件，可以使用内置工具(如 Maven 和 Gradle)开发和测试 Spring Boot 应用程序。

## 2.SpringBoot 开发环境要求

### 2.1 构建工具

构建工具(本项目涉及的代码大部分会采用 Maven 作为包管理工具):

| **Build Tool** | **Version** |
| -------------- | ----------- |
| Maven          | 3.3+        |
| Gradle         | 4.4+        |

### 2.2 开发工具推荐

推荐使用 IDEA 进行开发。最好的 Java 后台开发编辑器，没有之一！

### 2.3 Web 服务器

Spring Boot 支持以下嵌入式 servlet 容器:

| **Name**     | **Servlet Version** |
| ------------ | ------------------- |
| Tomcat 9.0   | 4.0                 |
| Jetty 9.4    | 3.1                 |
| Undertow 2.0 | 4.0                 |

您还可以将 Spring 引导应用程序部署到任何 Servlet 3.1+兼容的 Web 容器中。

这就是你为什么可以通过直接像运行 普通 Java 项目一样运行 SpringBoot 项目。这样的确省事了很多，方便了我们进行开发，降低了学习难度。