package com.franky.community.tool;

public class RedisKeyUtil {
    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId) (给这条实体点赞的用户集合)
    public static String generateEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String generateUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体: 使用有序集合可以查看最近的关注
    // followee:userId:entityType -> zset(entityId,now)
    public static String generateFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String generateFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //生成登录验证码的key
    //这个时候还没登录，所以用ticket代替
    public static String generateKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
}
