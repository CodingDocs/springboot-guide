JPA 这部分内容上手很容易，但是涉及到的东西还是挺多的，网上大部分关于 JPA 的资料都不是特别齐全，大部分用的版本也是比较落后的。另外，我下面讲到了的内容也不可能涵盖所有 JPA 相关内容，我只是把自己觉得比较重要的知识点总结在了下面。我自己也是参考着官方文档写的，[官方文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference)非常详细了，非常推荐阅读一下。这篇文章可以帮助对 JPA 不了解或者不太熟悉的人来在实际项目中正确使用 JPA。

项目代码基于 Spring Boot 最新的 2.1.9.RELEASE 版本构建（截止到这篇文章写完），另外，新建项目就不多说了，前面的文章已经很详细介绍过。

## 1.相关依赖

我们需要下面这些依赖支持我们完成这部分内容的学习：

```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

## 2.配置数据库连接信息和JPA配置

下面的配置中需要单独说一下 `spring.jpa.hibernate.ddl-auto=create`这个配置选项。

这个属性常用的选项有四种：

1. `create`:每次重新启动项目都会重新创新表结构，会导致数据丢失
2. `create-drop`:每次启动项目创建表结构，关闭项目删除表结构
3. `update`:每次启动项目会更新表结构
4. `validate`:验证表结构，不对数据库进行任何更改

但是，**一定要不要在生产环境使用 ddl 自动生成表结构，一般推荐手写 SQL 语句配合 Flyway 来做这些事情。**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_jpa?useSSL=false&serverTimezone=CTT
spring.datasource.username=root
spring.datasource.password=123456
# 打印出 sql 语句
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create
spring.jpa.open-in-view=false
# 创建的表的 ENGINE 为 InnoDB
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL55Dialect
```

## 3.实体类

我们为这个类添加了 `@Entity` 注解代表它是数据库持久化类，还配置了主键 id。

```java
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer age;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

}
```

如何检验你是否正确完成了上面 3 步?很简单，运行项目，查看数据如果发现控制台打印出创建表的 sql 语句，并且数据库中表真的被创建出来的话，说明你成功完成前面 3 步。

控制台打印出来的 sql 语句类似下面这样：

```sql
drop table if exists person
CREATE TABLE `person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL，
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
alter table person add constraint UK_p0wr4vfyr2lyifm8avi67mqw5 unique (name)
```

## 4.创建操作数据库的 Repository 接口

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
```

首先这个接口加了 `@Repository` 注解，代表它和数据库操作有关。另外，它继承了 `JpaRepository<Person, Long>`接口，而`JpaRepository<Person, Long>`长这样：

```java

@NoRepositoryBean
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    List<T> findAll();

    List<T> findAll(Sort var1);

    List<T> findAllById(Iterable<ID> var1);

    <S extends T> List<S> saveAll(Iterable<S> var1);

    void flush();

    <S extends T> S saveAndFlush(S var1);

    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

    T getOne(ID var1);

    <S extends T> List<S> findAll(Example<S> var1);

    <S extends T> List<S> findAll(Example<S> var1, Sort var2);
}
```

这表明我们只要继承了` JpaRepository<T, ID>` 就具有了 JPA 为我们提供好的增删改查、分页查询以及根据条件查询等方法。

### 4.1 JPA 自带方法实战

#### 1) 增删改查

**1.保存用户到数据库**

```java
    Person person = new Person("SnailClimb", 23);
    personRepository.save(person);
```

`save()`方法对应 sql 语句就是:`insert into person (age, name) values (23,"snailclimb")`

**2.根据 id 查找用户**

```java
    Optional<Person> personOptional = personRepository.findById(id);
```

`findById()`方法对应 sql 语句就是：`select * from person p where p.id = id`

**3.根据 id 删除用户**

```java
    personRepository.deleteById(id);
```

`deleteById()`方法对应 sql 语句就是：`delete from person where id=id`

**4.更新用户**

更新操作也要通过 `save()`方法来实现，比如：

```java
    Person person = new Person("SnailClimb", 23);
    Person savedPerson = personRepository.save(person);
    // 更新 person 对象的姓名
    savedPerson.setName("UpdatedName");
    personRepository.save(savedPerson);
```

在这里 `save()`方法相当于 sql 语句：`update person set name="UpdatedName" where id=id`

#### 2) 带条件的查询

下面这些方法是我们根据 JPA 提供的语法自定义的，你需要将下面这些方法写到 `PersonRepository` 中。

假如我们想要根据 Name 来查找 Person ，你可以这样：

```java
    Optional<Person> findByName(String name);
```

如果你想要找到年龄大于某个值的人，你可以这样：

```java
    List<Person> findByAgeGreaterThan(int age);
```

### 4.2 自定义 SQL 语句实战

很多时候我们自定义 sql 语句会非常有用。

根据 name 来查找 Person：

```java
    @Query("select p from Person p where p.name = :name")
    Optional<Person> findByNameCustomeQuery(@Param("name") String name);
```

Person 部分属性查询，避免 `select *`操作： 

```java
    @Query("select p.name from Person p where p.id = :id")
    String findPersonNameById(@Param("id") Long id);
```

根据 id 更新Person name：

```java

    @Modifying
    @Transactional
    @Query("update Person p set p.name = ?1 where p.id = ?2")
    void updatePersonNameById(String name, Long id);
```

### 4.3 创建异步方法

如果我们需要创建异步方法的话，也比较方便。

异步方法在调用时立即返回，然后会被提交给`TaskExecutor`执行。当然你也可以选择得出结果后才返回给客户端。如果对 Spring Boot 异步编程感兴趣的话可以看这篇文章：[《新手也能看懂的 SpringBoot 异步编程指南》](https://snailclimb.gitee.io/springboot-guide/#/./docs/advanced/springboot-async) 。

```java
@Async
Future<User> findByName(String name);               

@Async
CompletableFuture<User> findByName(String name); 
```

## 5.测试类和源代码地址

测试类：

```java

@SpringBootTest
@RunWith(SpringRunner.class)
public class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;
    private Long id;

    /**
     * 保存person到数据库
     */
    @Before
    public void setUp() {
        assertNotNull(personRepository);
        Person person = new Person("SnailClimb", 23);
        Person savedPerson = personRepository.saveAndFlush(person);// 更新 person 对象的姓名
        savedPerson.setName("UpdatedName");
        personRepository.save(savedPerson);

        id = savedPerson.getId();
    }

    /**
     * 使用 JPA 自带的方法查找 person
     */
    @Test
    public void should_get_person() {
        Optional<Person> personOptional = personRepository.findById(id);
        assertTrue(personOptional.isPresent());
        assertEquals("SnailClimb", personOptional.get().getName());
        assertEquals(Integer.valueOf(23), personOptional.get().getAge());

        List<Person> personList = personRepository.findByAgeGreaterThan(18);
        assertEquals(1, personList.size());
        // 清空数据库
        personRepository.deleteAll();
    }

    /**
     * 自定义 query sql 查询语句查找 person
     */

    @Test
    public void should_get_person_use_custom_query() {
        // 查找所有字段
        Optional<Person> personOptional = personRepository.findByNameCustomeQuery("SnailClimb");
        assertTrue(personOptional.isPresent());
        assertEquals(Integer.valueOf(23), personOptional.get().getAge());
        // 查找部分字段
        String personName = personRepository.findPersonNameById(id);
        assertEquals("SnailClimb", personName);
        System.out.println(id);
        // 更新
        personRepository.updatePersonNameById("UpdatedName", id);
        Optional<Person> updatedName = personRepository.findByNameCustomeQuery("UpdatedName");
        assertTrue(updatedName.isPresent());
        // 清空数据库
        personRepository.deleteAll();
    }

}
```

源代码地址：https://github.com/Snailclimb/springboot-guide/tree/master/source-code/basis/jpa-demo

## 6. 总结

本文主要介绍了 JPA 的基本用法：

1. 使用 JPA 自带的方法进行增删改查以及条件查询。

2. 自定义 SQL  语句进行查询或者更新数据库。

3. 创建异步的方法。

   

在下一篇关于 JPA 的文章中我会介绍到非常重要的两个知识点：

1. 基本分页功能实现
2. 多表联合查询以及多表联合查询下的分页功能实现。

