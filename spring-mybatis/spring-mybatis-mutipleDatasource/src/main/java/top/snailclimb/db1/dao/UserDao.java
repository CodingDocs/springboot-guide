package top.snailclimb.db1.dao;


import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Qualifier;
import top.snailclimb.bean.User;

import java.util.List;



@Qualifier("db1SqlSessionTemplate")
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findUserByName(String name);

}
