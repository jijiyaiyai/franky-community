package com.franky.community.service;


import com.franky.community.dao.DiscussPostMapper;
import com.franky.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int ordermod){
        return discussPostMapper.selectDiscussPosts_bypage(userId,offset,limit,ordermod);
    }

    public int findDiscussPostCount(int userId){
        return discussPostMapper.selectDiscussPost_count(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id,score);
    }

    public DiscussPost editDiscussPost(int discussPostId, String title, String content, Date date) {
        return discussPostMapper.updatePost(discussPostId,title,content,date);
    }

    public void updateTime(int discussPostId, Date commentTime) {
        discussPostMapper.updateTime(discussPostId,commentTime);
    }
}
