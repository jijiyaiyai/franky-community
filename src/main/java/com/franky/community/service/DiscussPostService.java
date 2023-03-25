package com.franky.community.service;


import com.franky.community.dao.DiscussPostMapper;
import com.franky.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts_bypage(userId,offset,limit);
    }

    public int findDiscussPostCount(int userId){
        return discussPostMapper.selectDiscussPost_count(userId);
    }
}
