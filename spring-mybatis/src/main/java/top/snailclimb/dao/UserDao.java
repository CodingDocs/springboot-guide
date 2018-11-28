package top.snailclimb.dao;


import org.apache.ibatis.annotations.*;
import top.snailclimb.bean.User;

import java.util.List;


@Mapper
public interface UserDao {
    /**
     * 通过名字查询用户
     *
     * @param name
     *            用户姓名
     * @return 查询到的用户
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findUserByName(@Param("name") String name);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM user")
    List<User> findAllUser();

    /**
     * 插入用户
     *
     * @param name
     *            姓名
     * @param age
     *            年龄
     */
    @Insert("INSERT INTO user(name, age,money) VALUES(#{name}, #{age}, #{money})")
    void insertUser(@Param("name") String name, @Param("age") Integer age, @Param("money") Double money);

    /**
     * 插入用户
     *
     * @param name
     *            姓名
     * @param age
     *            年龄
     */
    @Update("UPDATE  user SET name = #{name},age = #{age},money= #{money} WHERE id = #{id}")
    void updateUser(@Param("name") String name, @Param("age") Integer age, @Param("money") Double money,
                    @Param("id") int id);
}
