### JDK

截止到目前Spring Boot 的最新版本：2.1.8.RELEASE 要求 JDK 版本在 1.8 以上，所以确保你的电脑已经正确下载安装配置了 JDK（推荐 JDK 1.8 版本）。

### 构建工具

构建工具(本项目涉及的代码大部分会采用 Maven 作为包管理工具):

| **Build Tool** | **Version** |
| -------------- | ----------- |
| Maven          | 3.3+        |
| Gradle         | 4.4+        |

### 开发工具推荐

推荐使用 IDEA 进行开发。最好的 Java 后台开发编辑器，没有之一！

### Web 服务器

Spring Boot支持以下嵌入式servlet容器:

| **Name**     | **Servlet Version** |
| ------------ | ------------------- |
| Tomcat 9.0   | 4.0                 |
| Jetty 9.4    | 3.1                 |
| Undertow 2.0 | 4.0                 |

您还可以将Spring引导应用程序部署到任何Servlet 3.1+兼容的 Web 容器中。

这就是你为什么可以通过直接像运行 普通 Java 项目一样运行 SpringBoot 项目。这样的确省事了很多，方便了我们进行开发，降低了学习难度。