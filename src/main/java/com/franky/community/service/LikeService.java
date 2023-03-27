package com.franky.community.service;

import com.franky.community.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;


@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        //保证事务性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //生成entity和user的key
                String entityLikeKey = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
                //为了避免查询数据库拉低redis性能，让前端直接告诉我模型中帖子发布者的ID
                String userLikeKey = RedisKeyUtil.generateUserLikeKey(entityUserId);
                //判断一下在不在集合，在的话如果又点了，就是想取消
                //不在的话，就是想点赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                //开始事务
                operations.multi();

                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                //执行事务
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态（无状态、赞、踩）
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.generateUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        if(count == null){
            return 0;
        }else{
            return count;
        }
    }
}
