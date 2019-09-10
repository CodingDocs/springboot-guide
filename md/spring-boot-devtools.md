后端开发中热部署有很多方式，但是在开发 SpringBoot 项目有一种 Spring Boot 给我们提供好的很方便的一种方式，配置起来也很简单。

> 热部署可以简单的这样理解：我们修改程序代码后不需要重新启动程序，就可以获取到最新的代码，更新程序对外的行为。

热部署在我们日常开发可以为我们节省很多时间，通常我们在开发后端的过程中，当我们修改了后端代码之后都需要重启一下项目，这为我们浪费了时间，特别是在项目比较庞大，需要耗费大量时间的启动的时候。**这种方式好像消耗性能挺大的，也需要慎重使用。**

下面介绍一下如何通过 SpringBoot 提供的 spring-boot-devtools 实现简单的热部署。

**依赖:**

Maven:

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
			<optional>true</optional>
		</dependency>
```

```xml
  <plugin>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-maven-plugin</artifactId>
  </plugin>
```

Gradle:

```groovy

configurations {
    developmentOnly
    runtimeClasspath {
        extendsFrom developmentOnly
    }
}
dependencies {
      developmentOnly("org.springframework.boot:spring-boot-devtools")
}

```



**添加配置：**

ctrl+,(Win) / cmd+（Mac）打开项目配置：

输入 Compiler , 并且勾选上 Build project automatically

![dev-tools-idea1](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/dev-tools-idea1.png)

输入快捷键 ctrl + shift + alt + / （Win）cmd+option+shift+/(Mac)，并且选择 Registry

![dev-tools-idea2](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/dev-tools-idea2.png)

然后勾选上 Compiler autoMake allow when app running

![dev-tools-idea3](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/dev-tools-idea3.png)

很简单，这样你每次修改程序之后就不用重新启动了。