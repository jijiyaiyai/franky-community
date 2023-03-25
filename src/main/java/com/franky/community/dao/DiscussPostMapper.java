package com.franky.community.dao;

import com.franky.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //分页获取帖子，userID将被用于开发个人主页功能
    List<DiscussPost> selectDiscussPosts_bypage(int userId,int offset, int limit);

    //要使用动态sql而且只有一个参数的话就必须取别名
    int selectDiscussPost_count(@Param("userId") int userId);
}
