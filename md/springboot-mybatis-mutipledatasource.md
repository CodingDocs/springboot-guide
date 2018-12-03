

在上一篇文章[《优雅整合 SpringBoot+Mybatis ,可能是你见过最详细的一篇》](https://github.com/Snailclimb/springboot-integration-examples/blob/master/md/springboot-mybatis.md)中，带着大家整合了 SpringBoot 和 Mybatis ,我们在当时使用的时单数据源的情况，这种情况下 Spring Boot的配置非常简单，只需要在 application.properties 文件中配置数据库的相关连接参数即可。但是往往随着业务量发展，我们通常会进行数据库拆分或是引入其他数据库，从而我们需要配置多个数据源。下面基于 SpringBoot+Mybatis ，带着大家看一下 SpringBoot 中如何配置多数据源。

这篇文章所涉及的代码其实是基于上一篇文章[《优雅整合 SpringBoot+Mybatis ,可能是你见过最详细的一篇》](https://github.com/Snailclimb/springboot-integration-examples/blob/master/md/springboot-mybatis.md) 的项目写的，但是为了考虑部分读者没有读过上一篇文章，所以我还是会一步一步带大家走完每一步，力争新手也能在看完之后独立实践。

**目录：**

<!-- MarkdownTOC -->

- [一 开发前的准备](#一-开发前的准备)
  - [1.1 环境参数](#11-环境参数)
  - [1.2 创建工程](#12-创建工程)
  - [1.3 创建两个数据库和 user 用户表、money工资详情表](#13-创建两个数据库和-user-用户表、money工资详情表)
  - [1.4 配置 pom 文件中的相关依赖](#14-配置-pom-文件中的相关依赖)
  - [1.5 配置 application.properties](#15-配置-applicationproperties)
  - [1.6 创建用户类 Bean和工资详情类 Bean](#16-创建用户类-bean和工资详情类-bean)
- [二 数据源配置](#二-数据源配置)
- [三 Dao 层开发和 Service 层开发](#三-dao-层开发和-service-层开发)
  - [3.1 Dao 层](#31-dao-层)
  - [3.2 Service 层](#32-service-层)
- [四 Controller层](#四-controller层)
- [五 启动类](#五-启动类)

<!-- /MarkdownTOC -->


## 一 开发前的准备

### 1.1 环境参数

- 开发工具：IDEA
- 基础工具：Maven+JDK8
- 所用技术：SpringBoot+Mybatis
- 数据库：MySQL
- SpringBoot版本：**2.1.0.** SpringBoot2.0之后会有一些小坑，这篇文章会给你介绍到。注意版本不一致导致的一些小问题。

### 1.2 创建工程

创建一个基本的 SpringBoot 项目，我这里就不多说这方面问题了，具体可以参考下面这篇文章：

[https://blog.csdn.net/qq_34337272/article/details/79563606](https://blog.csdn.net/qq_34337272/article/details/79563606)

本项目结构：

![基于SpirngBoot2.0+ 的 SpringBoot+Mybatis 多数据源配置项目结构](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-30/26886896.jpg)

### 1.3 创建两个数据库和 user 用户表、money工资详情表

我们一共创建的两个数据库，然后分别在这两个数据库中创建了 user 用户表、money工资详情表。

我们的用户表很简单，只有 4 个字段：用户 id、姓名、年龄、余额。如下图所示：

![用户表信息](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-29/99248060.jpg)

添加了“余额money”字段是为了给大家简单的演示一下事务管理的方式。

我们的工资详情表也很简单，也只有 4 个字段： id、基本工资、奖金和惩罚金。如下图所示：

![工资详情表信息](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-30/55857794.jpg)

**建表语句：**

用户表：

```sql
CREATE TABLE `user` (
  `id` int(13) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(33) DEFAULT NULL COMMENT '姓名',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `money` double DEFAULT NULL COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8
```

工资详情表：

```sql
CREATE TABLE `money` (
  `id` int(33) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `basic` int(33) DEFAULT NULL COMMENT '基本工资',
  `reward` int(33) DEFAULT NULL COMMENT '奖金',
  `punishment` int(33) DEFAULT NULL COMMENT '惩罚金',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8
```

### 1.4 配置 pom 文件中的相关依赖

由于要整合 springboot 和 mybatis 所以加入了artifactId 为 mybatis-spring-boot-starter 的依赖，由于使用了Mysql 数据库 所以加入了artifactId 为 mysql-connector-java 的依赖。

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
``` 

### 1.5 配置 application.properties

配置两个数据源:数据库1和数据库2！

注意事项：

在1.0 配置数据源的过程中主要是写成：spring.datasource.url 和spring.datasource.driverClassName。
而在2.0升级之后需要变更成：spring.datasource.jdbc-url和spring.datasource.driver-class-name！不然在连接数据库时可能会报下面的错误：

```
### Error querying database.  Cause: java.lang.IllegalArgumentException: jdbcUrl is required with driverClassName.
```

另外在在2.0.2+版本后需要在datasource后面加上hikari，如果你没有加的话，同样可能会报错。




```properties
server.port=8335
# 配置第一个数据源
spring.datasource.hikari.db1.jdbc-url=jdbc:mysql://127.0.0.1:3306/erp?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2B8
spring.datasource.hikari.db1.username=root
spring.datasource.hikari.db1.password=153963
spring.datasource.hikari.db1.driver-class-name=com.mysql.cj.jdbc.Driver
# 配置第二个数据源
spring.datasource.hikari.db2.jdbc-url=jdbc:mysql://127.0.0.1:3306/erp2?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2B8
spring.datasource.hikari.db2.username=root
spring.datasource.hikari.db2.password=153963
spring.datasource.hikari.db2.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 1.6 创建用户类 Bean和工资详情类 Bean

User.java

```java
public class User {
    private int id;
    private String name;
    private int age;
    private double money;
    ...
    此处省略getter、setter以及 toString方法
}
```

Money.java

```java
public class Money {
    private int basic;
    private int reward;
    private int punishment;
    ...
    此处省略getter、setter以及 toString方法
}
```


## 二 数据源配置

通过 Java 类来实现对两个数据源的配置，这一部分是最关键的部分了,这里主要提一下下面这几点：

- `@MapperScan` 注解中我们声明了使用数据库1的dao类所在的位置,还声明了 SqlSessionTemplate  。SqlSessionTemplate是MyBatis-Spring的核心。这个类负责管理MyBatis的SqlSession,调用MyBatis的SQL方法，翻译异常。SqlSessionTemplate是线程安全的，可以被多个DAO所共享使用。
- 由于我使用的是全注解的方式开发,所以下面这条找并且解析 mapper.xml 配置语句被我注释掉了
 `bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/db2/*.xml"));`
- 比如我们要声明使用数据1，直接在 dao 层的类上加上这样一个注释即可：`@Qualifier("db1SqlSessionTemplate")`
- 我们在数据库1配置类的每个方法前加上了 `@Primary` 注解来声明这个数据库时默认数据库，不然可能会报错。



DataSource1Config.java

```java
@Configuration
@MapperScan(basePackages = "top.snailclimb.db1.dao", sqlSessionTemplateRef = "db1SqlSessionTemplate")
public class DataSource1Config {

    /**
     * 生成数据源.  @Primary 注解声明为默认数据源
     */
    @Bean(name = "db1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db1")
    @Primary
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 SqlSessionFactory
     */
    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //  bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/db1/*.xml"));
        return bean.getObject();
    }

    /**
     * 配置事务管理
     */
    @Bean(name = "db1TransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("db1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "db1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}

```

DataSource2Config.java

```java
@Configuration
@MapperScan(basePackages = "top.snailclimb.db2.dao", sqlSessionTemplateRef = "db2SqlSessionTemplate")
public class DataSource2Config {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db2")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db2SqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("db2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/db2/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "db2TransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("db2DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "db2SqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("db2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
```

## 三 Dao 层开发和 Service 层开发 

新建两个不同的包存放两个不同数据库的 dao 和 service。


![](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-30/72856252.jpg)

### 3.1 Dao 层

对于两个数据库，我们只是简单的测试一个查询这个操作。在上一篇文章[《优雅整合 SpringBoot+Mybatis ,可能是你见过最详细的一篇》](https://github.com/Snailclimb/springboot-integration-examples/blob/master/md/springboot-mybatis.md)中，我带着大家使用注解实现了数据库基本的增删改查操作。

UserDao.java

```java
@Qualifier("db1SqlSessionTemplate")
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findUserByName(String name);

}
```

MoneyDao.java

```java

@Qualifier("db2SqlSessionTemplate")
public interface MoneyDao {

    /**
     * 通过id 查看工资详情
     */
    @Select("SELECT * FROM money WHERE id = #{id}")
    Money findMoneyById(@Param("id") int id);
}

```

### 3.2 Service 层

Service 层很简单，没有复杂的业务逻辑。

UserService.java

```java
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    /**
     * 根据名字查找用户
     */
    public User selectUserByName(String name) {
        return userDao.findUserByName(name);
    }

}
```

MoneyService.java

```java
@Service
public class MoneyService {
    @Autowired
    private MoneyDao moneyDao;

    /**
     * 根据名字查找用户
     */
    public Money selectMoneyById(int id) {
        return moneyDao.findMoneyById(id);
    }

}

```


## 四 Controller层

Controller 层也非常简单。

UserController.java

```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/query")
    public User testQuery() {
        return userService.selectUserByName("Daisy");
    }
}
```

MoneyController.java

```java
@RestController
@RequestMapping("/money")
public class MoneyController {
    @Autowired
    private MoneyService moneyService;

    @RequestMapping("/query")
    public Money testQuery() {
        return moneyService.selectMoneyById(1);
    }
}
```

## 五 启动类

```java
//此注解表示SpringBoot启动类
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
```

这样基于SpirngBoot2.0+ 的 SpringBoot+Mybatis 多数据源配置就已经完成了， 两个数据库都可以被访问了。

