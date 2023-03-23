package com.franky.community.dao;


import com.franky.community.FrankyCommunityApplication;
import com.franky.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FrankyCommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectByName(){
        System.out.println(userMapper.selectByName("aaa"));
        System.out.println(userMapper.selectByName("bbb"));
        System.out.println(userMapper.selectByName("ccc"));
        System.out.println(userMapper.selectByName("asdf"));
    }

   @Test
    public void testInsertUser(){
       User user = new User("asdf","asdf","asdf","asdf",0,1,
               "asdf","asdf",new Date());
       userMapper.insertUser(user);
       System.out.println(userMapper.selectByName("asdf"));
   }
}
