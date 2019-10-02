## 一 SpringBoot介绍

### 1.1  先从Spring谈起
我们知道Spring是重量级企业开发框架 **Enterprise JavaBean（EJB）** 的替代平，Spring为企业级Java开发提供了一种相对简单的方法，通过 **依赖注入** 和 **面向切面编程** ，用简单的 **Java对象（Plain Old Java Object，POJO）** 实现了EJB的功能

**虽然Spring的组件代码是轻量级的，但它的配置却是重量级的（需要大量XML配置）** 。Spring 2.5引入了基于注解的组件扫描，这消除了大量针对应用程序自身组件的显式XML配置。Spring 3.0引入了基于Java的配置，这是一种类型安全的可重构配置方式，可以代替XML。

尽管如此，我们依旧没能逃脱配置的魔爪。开启某些Spring特性时，比如事务管理和Spring MVC，还是需要用XML或Java进行显式配置。启用第三方库时也需要显式配置，比如基于Thymeleaf的Web视图。配置Servlet和过滤器（比如Spring的DispatcherServlet）同样需要在web.xml或Servlet初始化代码里进行显式配置。组件扫描减少了配置量，Java配置让它看上去简洁不少，但Spring还是需要不少配置。

光配置这些XML文件都够我们头疼的了，占用了我们大部分时间和精力。除此之外，相关库的依赖非常让人头疼，不同库之间的版本冲突也非常常见。

**不过，好消息是：Spring Boot让这一切成为了过去。**

### 1.2  再来谈谈Spring Boot


**最好直白的介绍莫过于官方的介绍：**

> Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can “just run”...Most Spring Boot applications need very little Spring configuration.(Spring Boot可以轻松创建独立的生产级基于Spring的应用程序,只要通过 “just run”（可能是run ‘Application’或java -jar 或 tomcat 或 maven插件run 或 shell脚本）便可以运行项目。大部分Spring Boot项目只需要少量的配置即可)

**关于Spring Boot的一些误解：**

- **Spring Boot不是应用服务器：** Spring Boot可以把Web应用程序变为可自执行的JAR文件，不用部署到传统Java应用服务器里就能在命令行里运行。Spring Boot在应用程序里嵌入了一个Servlet容器（Tomcat，Jetty或Undertow），以此实现这一功能。但这是内嵌的Servlet容器提供的功能，不是Spring Boot实现的
-  **Spring Boot也没有实现诸如JPA或JMS（Java Message Service，Java消息服务）之类的企业级Java规范。** 它的确支持不少企业级Java规范，但是要在Spring里自动配置支持那些特性的Bean。例如，Spring Boot没有实现JPA，不过它自动配置了某个JPA实现（比如Hibernate）的Bean，以此支持JPA
- **Spring Boot没有引入任何形式的代码生成，而是利用了Spring 4的条件化配置特性，以及Maven和Gradle提供的传递依赖解析，以此实现Spring应用程序上下文里的自动配置。**


**简而言之，从本质上来说，Spring Boot就是Spring，它做了那些没有它你自己也会去做的Spring Bean配置。**

### Spring Boot的主要优点

1. 开发基于 Spring 的应用程序很容易。
2. Spring Boot 项目所需的开发或工程时间明显减少，通常会提高整体生产力。
3. Spring Boot不需要编写大量样板代码、XML配置和注释。
4. Spring引导应用程序可以很容易地与Spring生态系统集成，如Spring JDBC、Spring ORM、Spring Data、Spring Security等。
5. Spring Boot遵循“固执己见的默认配置”，以减少开发工作（默认配置可以修改）。
6. Spring Boot 应用程序提供嵌入式HTTP服务器，如Tomcat和Jetty，可以轻松地开发和测试web应用程序。（这点很赞！普通运行Java程序的方式就能运行基于Spring Boot web 项目，省事很多）
7. Spring Boot提供命令行接口(CLI)工具，用于开发和测试Spring Boot应用程序，如Java或Groovy。
8. Spring Boot提供了多种插件，可以使用内置工具(如Maven和Gradle)开发和测试Spring Boot应用程序。