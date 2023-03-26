package com.franky.community.control;


import com.franky.community.entity.Message;
import com.franky.community.entity.Page;
import com.franky.community.entity.User;
import com.franky.community.service.MessageService;
import com.franky.community.service.UserService;
import com.franky.community.tool.CommunityUtil;
import com.franky.community.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 全部会话
        List<Map<String, Object>> conversations = new ArrayList<>();
        // 会话详细内容列表（每个会话只有最新的评论）
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                String convId = message.getConversationId();
                int userId = user.getId();
                int fromId = message.getFromId();
                int toId = message.getToId();

                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(convId));
                map.put("unreadCount", messageService.findLetterUnreadCount(userId, convId));
                //识别一下是谁发的
                int targetId = userId == fromId ? toId : fromId;
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表(含有这个会话的全部消息)
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        //将这个会话中未读的全部设置为已读
        assert letterList != null;
        List<Integer>ids = getUnreadLetterIDs(letterList);
        if(!ids.isEmpty()){
            messageService.readMessages(ids);
        }

        return "/site/letter-detail";
    }

    //找到私信的目标ID，返回这个ID对应的user对象
    private User getLetterTarget(String conversationId) {
        //conversationID 的结构是发方ID_收方ID，小的ID在前
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        return hostHolder.getUser().getId() == id0 ?
                userService.findUserById(id1)
                : userService.findUserById(id0);

    }
    private List<Integer> getUnreadLetterIDs(List<Message> letterList){
        List<Integer>ids = new ArrayList<>();
        for (Message m: letterList){
            if(hostHolder.getUser().getId() == m.getToId()){
                if(m.getStatus() == 0){
                    ids.add(m.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path="/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        //构造会话ID时，永远保持小号在前，这样双方的会话ID就是一致的
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }
}
