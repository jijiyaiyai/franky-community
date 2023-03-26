package com.franky.community.control;

import com.franky.community.entity.Comment;
import com.franky.community.entity.DiscussPost;
import com.franky.community.entity.Page;
import com.franky.community.entity.User;
import com.franky.community.service.CommentService;
import com.franky.community.service.DiscussPostService;
import com.franky.community.service.UserService;
import com.franky.community.tool.CommunityConstant;
import com.franky.community.tool.CommunityUtil;
import com.franky.community.tool.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Page page, Model model) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 回复层主、回复楼中楼
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentViewObject = new HashMap<>();
                // 评论
                commentViewObject.put("comment", comment);
                // 作者
                commentViewObject.put("user", userService.findUserById(comment.getUserId()));

                // 针对某一楼的回复列表，有多少查多少
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyViewObject = new HashMap<>();
                        // 回复的内容
                        replyViewObject.put("reply", reply);
                        // 回复的作者
                        replyViewObject.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标：如果是0，就是直接回复层主，否则就是回复楼中楼，应该查询目标用户
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyViewObject.put("target", target);

                        replyVoList.add(replyViewObject);
                    }
                }
                commentViewObject.put("replies", replyVoList);

                // 这一楼的回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentViewObject.put("replyCount", replyCount);

                commentVoList.add(commentViewObject);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}

