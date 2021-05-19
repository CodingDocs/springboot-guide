

<!-- MarkdownTOC -->

- [一 开发前的准备](#一-开发前的准备)
  - [1.1 环境参数](#11-环境参数)
  - [1.2 创建工程](#12-创建工程)
  - [1.3 创建数据库和 user 用户表](#13-创建数据库和-user-用户表)
  - [1.4 配置 pom 文件中的相关依赖](#14-配置-pom-文件中的相关依赖)
  - [1.5 配置 application.properties](#15-配置-applicationproperties)
  - [1.6 创建用户类 Bean](#16-创建用户类-bean)
- [二 全注解的方式](#二-全注解的方式)
  - [2.1 Dao 层开发](#21-dao-层开发)
  - [2.2 service 层](#22-service-层)
  - [2.3 Controller 层](#23-controller-层)
  - [2.4 启动类](#24-启动类)
  - [2.5 简单测试](#25-简单测试)
- [三 xml 的方式](#三-xml-的方式)
  - [3.1 Dao 层的改动](#31-dao-层的改动)
  - [3.2 配置文件的改动](#32-配置文件的改动)

<!-- /MarkdownTOC -->

SpringBoot 整合 Mybatis 有两种常用的方式，一种就是我们常见的 xml 的方式 ，还有一种是全注解的方式。我觉得这两者没有谁比谁好，在 SQL 语句不太长的情况下，我觉得全注解的方式一定是比较清晰简洁的。但是，复杂的 SQL 确实不太适合和代码写在一起。

下面就开始吧！

## 一 开发前的准备

### 1.1 环境参数

- 开发工具：IDEA
- 基础工具：Maven+JDK8
- 所用技术：SpringBoot+Mybatis
- 数据库：MySQL
- SpringBoot版本：2.1.0

### 1.2 创建工程

创建一个基本的 SpringBoot 项目，我这里就不多说这方面问题了，具体可以参考前面的文章。


### 1.3 创建数据库和 user 用户表

我们的用户表很简单，只有 4 个字段：用户 id、姓名、年龄、余额，如下图所示：

![表信息](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-29/99248060.jpg)

添加了“余额money”字段是为了给大家简单的演示一下事务管理的方式。

**建表语句：**

```sql
CREATE TABLE `user` (
  `id` int(13) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(33) DEFAULT NULL COMMENT '姓名',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `money` double DEFAULT NULL COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8
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

由于我使用的是比较新的Mysql连接驱动，所以配置文件可能和之前有一点不同。

```properties
server.port=8333
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/erp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

注意：我们使用的 mysql-connector-java 8+ ，JDBC 连接到mysql-connector-java 6+以上的需要指定时区 `serverTimezone=GMT%2B8`。另外我们之前使用配置 Mysql数据连接是一般是这样指定`driver-class-name=com.mysql.jdbc.Driver`,但是现在不可以必须为 否则控制台下面的异常：

```error
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
```

上面异常的意思是：`com.mysql.jdbc.Driver` 被弃用了。新的驱动类是 `com.mysql.cj.jdbc.Driver`。驱动程序通过SPI自动注册，手动加载类通常是不必要。

如果你非要写把`com.mysql.jdbc.Driver` 改为`com.mysql.cj.jdbc.Driver `即可。

### 1.6 创建用户类 Bean

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

## 二 全注解的方式

先来看一下 全注解的方式，这种方式和后面提到的 xml 的方式的区别仅仅在于 一个将 sql 语句写在 java 代码中，一个写在 xml 配置文件中。全注方式解转换成 xml 方式仅需做一点点改变即可，我在后面会提到。

**项目结构：**

![全注解方式项目结构](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-29/1909910.jpg)

### 2.1 Dao 层开发


UserDao.java

```java
@Mapper
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findUserByName(@Param("name") String name);

    /**
     * 查询所有用户信息
     */
    @Select("SELECT * FROM user")
    List<User> findAllUser();

    /**
     * 插入用户信息
     */
    @Insert("INSERT INTO user(name, age,money) VALUES(#{name}, #{age}, #{money})")
    void insertUser(@Param("name") String name, @Param("age") Integer age, @Param("money") Double money);

    /**
     * 根据 id 更新用户信息
     */
    @Update("UPDATE  user SET name = #{name},age = #{age},money= #{money} WHERE id = #{id}")
    void updateUser(@Param("name") String name, @Param("age") Integer age, @Param("money") Double money,
                    @Param("id") int id);

    /**
     * 根据 id 删除用户信息
     */
    @Delete("DELETE from user WHERE id = #{id}")
    void deleteUser(@Param("id") int id);
}
```

### 2.2 service 层

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

    /**
     * 查找所有用户
     */
    public List<User> selectAllUser() {
        return userDao.findAllUser();
    }

    /**
     * 插入两个用户
     */
    public void insertService() {
        userDao.insertUser("SnailClimb", 22, 3000.0);
        userDao.insertUser("Daisy", 19, 3000.0);
    }

    /**
     * 根据id 删除用户
     */

    public void deleteService(int id) {
        userDao.deleteUser(id);
    }

    /**
     * 模拟事务。由于加上了 @Transactional注解，如果转账中途出了意外 SnailClimb 和 Daisy 的钱都不会改变。
     */
    @Transactional
    public void changemoney() {
        userDao.updateUser("SnailClimb", 22, 2000.0, 3);
        // 模拟转账过程中可能遇到的意外状况
        int temp = 1 / 0;
        userDao.updateUser("Daisy", 19, 4000.0, 4);
    }
}

```

### 2.3 Controller 层

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

    @RequestMapping("/insert")
    public List<User> testInsert() {
        userService.insertService();
        return userService.selectAllUser();
    }


    @RequestMapping("/changemoney")
    public List<User> testchangemoney() {
        userService.changemoney();
        return userService.selectAllUser();
    }

    @RequestMapping("/delete")
    public String testDelete() {
        userService.deleteService(3);
        return "OK";
    }

}
```

### 2.4 启动类

```java
//此注解表示SpringBoot启动类
@SpringBootApplication
// 此注解表示动态扫描DAO接口所在包，实际上不加下面这条语句也可以找到
@MapperScan("top.snailclimb.dao")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
```

### 2.5 简单测试

上述代码经过测试都没问题，这里贴一下根据姓名查询的测试的结果。

![根据姓名查询的测试的结果](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-29/92834920.jpg)

## 三 xml 的方式

**项目结构：**
![项目结构](http://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-11-29/485492.jpg)

相比于注解的方式主要有以下几点改变，非常容易实现。

### 3.1 Dao 层的改动

我这里只演示一个根据姓名找人的方法。

**UserDao.java**

```java
@Mapper
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    User findUserByName(String name);

}
```

**UserMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="top.snailclimb.dao.UserDao">

    <select id="findUserByName" parameterType="String" resultType="top.snailclimb.bean.User">
        SELECT * FROM user WHERE name = #{name}
    </select>
</mapper>

```

### 3.2 配置文件的改动

配置文件中加入下面这句话：

`mybatis.mapper-locations=classpath:mapper/*.xml`



代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/basis/springboot-mybatis