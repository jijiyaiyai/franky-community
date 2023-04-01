package com.franky.community.service;

import com.franky.community.dao.LoginTicketMapper;
import com.franky.community.dao.UserMapper;
import com.franky.community.entity.LoginTicket;
import com.franky.community.entity.User;
import com.franky.community.tool.CommunityConstant;
import com.franky.community.tool.CommunityUtil;
import com.franky.community.tool.MailClient;
import com.franky.community.tool.RedisKeyUtil;
import javax.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}") //获取网站的域名
    private String domain;

    @Value("${server.servlet.context-path}") //获取项目的应用路径
    private String contextPath;


    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.generateUserIDKey(userId);
        User user = (User) redisTemplate.opsForValue().get(redisKey);
        return user==null ? initCache(userId) : user;

    }
    private User getCache(String userName) {
        String redisKey = RedisKeyUtil.generateUserNameKey(userName);
        User user = (User) redisTemplate.opsForValue().get(redisKey);
        return user==null ? initCache(userName) : user;

    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.generateUserIDKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    private User initCache(String userName) {
        User user = userMapper.selectByName(userName);
        String redisKey = RedisKeyUtil.generateUserNameKey(userName);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.generateUserIDKey(userId);
        redisTemplate.delete(redisKey);
    }
    private void clearCache(String userName) {
        String redisKey = RedisKeyUtil.generateUserNameKey(userName);
        redisTemplate.delete(redisKey);
    }

    public User findUserById(int id){
        //return userMapper.selectById(id);
        return getCache(id);
    }

    public User findUserByName(String name){
        //return userMapper.selectByName(name);
        return getCache(name);
    }

    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(Ticket);
        String redisKey = RedisKeyUtil.generateTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //获取用户权限标识，转化为字符串
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add((GrantedAuthority) () -> {
            switch (user.getType()) {
                case 1:
                    return AUTHORITY_ADMIN;
                case 2:
                    return AUTHORITY_MODERATOR;
                default:
                    return AUTHORITY_USER;
            }
        });
        return list;
    }

    //注册服务 /////////////////////////////////////////////////////////////////////

    /**
    传入User对象（也就是在表单那里封装好，提交上来）然后进行各项空值的判断等等
    */
    public Map<String, Object> register(User user) throws MessagingException {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        //随机生成盐
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //往他的密码中加盐生成md5码
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        //激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("http://localhost:8080/community/user/header/defaultHead.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        // 生成激活链接
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        //查询当前这个用户，如果已经存在说明是重复激活了
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }
        //不然就对比激活码，相同才能激活
        else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.generateTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket); // redis会序列化ticket对象


        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //登出服务 //////////////////////////////////////////////////////////////////////
    public void logout(String ticket) {
        //直接把ticket设置为无效就好
        //只是修改状态而不是直接删掉，比较耗费时间，但是可以查登录的记录
        String redisKey = RedisKeyUtil.generateTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        //删掉只需一行
        //redisTemplate.opsForValue().getAndDelete(redisKey);

        //loginTicketMapper.updateStatus(ticket, 1);

    }

    //更新头像服务 //////////////////////////////////////////////////////////////////
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
        //return userMapper.updateHeader(userId, headerUrl);
    }
}
