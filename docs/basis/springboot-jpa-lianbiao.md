# JPA 连表查询和分页

对于连表查询，在 JPA 中还是非常常见的，由于  JPA 可以在 respository 层自定义 SQL 语句，所以通过自定义 SQL 语句的方式实现连表还是挺简单。这篇文章是在上一篇[入门 JPA](./springboot-jpa)的文章的基础上写的，不了解 JPA 的可以先看上一篇文章。

在[上一节](./springboot-jpa)的基础上我们新建了两个实体类，如下：

## 相关实体类创建

`Company.java`

```java
@Entity
@Data
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String companyName;
    private String description;

    public Company(String name, String description) {
        this.companyName = name;
        this.description = description;
    }
}
```

`School.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
}
```

## 自定义 SQL语句实现连表查询

假如我们当前要通过 person 表的 id 来查询 Person 的话，我们知道 Person 的信息一共分布在`Company`、`School`、`Person`这三张表中，所以，我们如果要把 Person 的信息都查询出来的话是需要进行连表查询的。

首先我们需要创建一个包含我们需要的 Person 信息的 DTO 对象,我们简单第将其命名为 `UserDTO`，用于保存和传输我们想要的信息。

```java
@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDTO {
    private String name;
    private int age;
    private String companyName;
    private String schoolName;
}
```

下面我们就来写一个方法查询出 Person 的基本信息。

```java
    /**
     * 连表查询
     */
    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " +
            "from Person p left join Company c on  p.companyId=c.id " +
            "left join School s on p.schoolId=s.id " +
            "where p.id=:personId")
    Optional<UserDTO> getUserInformation(@Param("personId") Long personId);
```

可以看出上面的 sql 语句和我们平时写的没啥区别，差别比较大的就是里面有一个 new 对象的操作。

## 自定义 SQL 语句连表查询并实现分页操作

假如我们要查询当前所有的人员信息并实现分页的话，你可以按照下面这种方式来做。可以看到，为了实现分页，我们在`@Query`注解中还添加了 **countQuery** 属性。

```java
@Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " +
        "from Person p left join Company c on  p.companyId=c.id " +
        "left join School s on p.schoolId=s.id ",
        countQuery = "select count(p.id) " +
                "from Person p left join Company c on  p.companyId=c.id " +
                "left join School s on p.schoolId=s.id ")
Page<UserDTO> getUserInformationList(Pageable pageable);
```

实际使用：

```java
//分页选项
PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "age");
Page<UserDTO> userInformationList = personRepository.getUserInformationList(pageRequest);
//查询结果总数
System.out.println(userInformationList.getTotalElements());// 6
//按照当前分页大小，总页数
System.out.println(userInformationList.getTotalPages());// 2
System.out.println(userInformationList.getContent());
```

## 加餐:自定以SQL语句的其他用法

下面我只介绍两种比较常用的：

1. IN 查询
2. BETWEEN 查询

当然，还有很多用法需要大家自己去实践了。

### IN 查询

 在 sql 语句中加入我们需要筛选出符合几个条件中的一个的情况下，可以使用 IN 查询，对应到 JPA 中也非常简单。比如下面的方法就实现了，根据名字过滤需要的人员信息。

```java
@Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " +
        "from Person p left join Company c on  p.companyId=c.id " +
        "left join School s on p.schoolId=s.id " +
        "where p.name IN :peopleList")
List<UserDTO> filterUserInfo(List peopleList);
```

实际使用:

```java
List<String> personList=new ArrayList<>(Arrays.asList("person1","person2"));
List<UserDTO> userDTOS = personRepository.filterUserInfo(personList);
```

### BETWEEN 查询

查询满足某个范围的值。比如下面的方法就实现查询满足某个年龄范围的人员的信息。

```java
    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " +
            "from Person p left join Company c on  p.companyId=c.id " +
            "left join School s on p.schoolId=s.id " +
            "where p.age between :small and :big")
    List<UserDTO> filterUserInfoByAge(int small,int big);
```

实际使用：

```java
List<UserDTO> userDTOS = personRepository.filterUserInfoByAge(19,20);
```

## 总结

本节我们主要学习了下面几个知识点：

1. 自定义 SQL 语句实现连表查询；
2. 自定义 SQL 语句连表查询并实现分页操作；
3. 条件查询：IN 查询，BETWEEN查询。
