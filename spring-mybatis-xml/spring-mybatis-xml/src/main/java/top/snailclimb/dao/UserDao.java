package top.snailclimb.dao;


import org.apache.ibatis.annotations.*;
import top.snailclimb.bean.User;

import java.util.List;


@Mapper
public interface UserDao {
    /**
     * 通过名字查询用户信息
     */
    User findUserByName(String name);

}
