package com.franky.community.dao;

import com.franky.community.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    //更新账号激活状态
    int updateStatus(int id, int status);

    //更新头像
    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
