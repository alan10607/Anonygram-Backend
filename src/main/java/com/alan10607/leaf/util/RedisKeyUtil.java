package com.alan10607.leaf.util;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
    //格式: leaf:類別:內容
    public static final String LEAF_KEY_PREFIX = "leaf:leafKey:%s";
    public static final String RW_LOCK_PREFIX = "leaf:rwLock:%s";
    public static final String LOCK_FIND_COUNT_FROM_DB = "leaf:lock:findCountFromDB";
    public static final String LOCK_SAVE_COUNT_TO_DB = "leaf:lock:saveCountToDB";
    public static final String SYSTEM_DB_LEAF_NAME = "leaf:system:dbLeafName";//用來記錄有哪些leaf要更新

    /**
     * Get leaf's hashKey
     * @param leafName
     * @return
     */
    public String leafKey(String leafName){
        return String.format(LEAF_KEY_PREFIX, leafName);
    }

    /**
     * Get leaf's readWriteLock key
     * @param lockName
     * @return
     */
    public String rwLock(String lockName){
        return String.format(RW_LOCK_PREFIX, lockName);
    }

    /**
     * Add random seconds to the expired time
     * @param sec
     * @return
     */
    public int getRandomExpire(int sec){
        return ((int) (Math.random() * 60)) + sec;//加上60秒
    }



    public static final String ART_SET = "data:artSet";
    public static final String ART_PREFIX = "data:art:%s";
    public static final String CONT_SET_PREFIX = "data:contSet:%s";
    public static final String CONT_PREFIX = "data:cont:%s:$s";
    public static final String USER_LIKE_PREFIX = "data:userLike:%s:%s";
    public static final String LIKE_COUNT_PREFIX = "data:likeCount:%s";
    public static final String LIKE_CONT_LOCK = "lock:likeCont";
    public String art(String artId){
        return String.format(ART_PREFIX, artId);
    }
    public String contSet(String artId){
        return String.format(CONT_SET_PREFIX, artId);
    }
    public String cont(String contId, String no){
        return String.format(CONT_PREFIX, contId, no);
    }
    public String userLike(String userId, String contId){
        return String.format(USER_LIKE_PREFIX, userId, contId);
    }
    public String likeCount(String contId){
        return String.format(LIKE_COUNT_PREFIX, contId);
    }

}