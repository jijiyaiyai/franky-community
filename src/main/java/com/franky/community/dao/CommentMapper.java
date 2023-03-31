package com.franky.community.dao;


import com.franky.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //entity指明了是谁的评论（帖子的、评论的）
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询这个实体对应了多少评论
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int entityId);

    List<Comment> selectCommentByUserId(int userId);
}
