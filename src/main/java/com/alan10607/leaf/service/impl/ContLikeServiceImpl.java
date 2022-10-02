package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.TxnService;
import com.alan10607.leaf.util.RedisKeyUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeServiceImpl implements ContLikeService {
    private TxnService txnService;
    private ContLikeDAO contLikeDAO;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil keyUtil;
    private final RedissonClient redisson;
    private final static int LIKE = 1;
    private final static int UNLIKE = 0;

    public boolean findContLikeFromRedis(String id, int no, String userId){
        String isLike = keyUtil.LikeValue(id, no, userId, LIKE);
        String unLike = keyUtil.LikeValue(id, no, userId, UNLIKE);

        //順序: LIKE_NEW > LIKE_BATCH > LIKE_STATIC > DB
        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_NEW, isLike))
            return true;

        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_NEW, unLike))
            return false;

        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_BATCH, isLike))
            return true;

        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_BATCH, unLike))
            return false;

        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_STATIC, isLike))
            return true;

        if(redisTemplate.opsForSet().isMember(keyUtil.LIKE_STATIC, unLike))
            return false;

        if(findIsLike(id, no, userId)) {
            redisTemplate.opsForSet().add(keyUtil.LIKE_STATIC, isLike);
            return true;
        }else {
            redisTemplate.opsForSet().add(keyUtil.LIKE_STATIC, unLike);
            return false;
        }
    }

    public boolean UpdateIsLikeFromRedis(String id, int no, String userId) throws Exception {
        String isLike = keyUtil.LikeValue(id, no, userId, LIKE);
        String unLike = keyUtil.LikeValue(id, no, userId, UNLIKE);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/toggleLike.lua")));
        redisScript.setResultType(Long.class);
        Long isSuccess = (Long) redisTemplate.execute(redisScript,
                Arrays.asList(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH, keyUtil.LIKE_STATIC),
                isLike, unLike);

        if(isSuccess == 0){
            log.error("Already like, skip this time, id={}, no={}, userId={}", id, no, userId);
        }else if(isSuccess == -1) {
            throw new RuntimeException(
                String.format("Update like by lua failed because set not found, isSuccess=-1, id=%s, no=%s, userId=%s", id, no, userId));
        }

        return isSuccess == 1;
    }

    public boolean UpdateUnLikeFromRedis(String id, int no, String userId) throws Exception {
        String isLike = keyUtil.LikeValue(id, no, userId, LIKE);
        String unLike = keyUtil.LikeValue(id, no, userId, UNLIKE);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/toggleLike.lua")));
        redisScript.setResultType(Long.class);
        Long isSuccess = (Long) redisTemplate.execute(redisScript,
                Arrays.asList(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH, keyUtil.LIKE_STATIC),
                unLike, isLike);

        if(isSuccess == 0){
            log.error("Already unlike, skip this time, id={}, no={}, userId={}", id, no, userId);
        }else if(isSuccess == -1) {
            throw new RuntimeException(
                String.format("Update like by lua failed because set not found, isSuccess=-1, id=%s, no=%s, userId=%s", id, no, userId));
        }

        return isSuccess == 1;
    }

    public boolean findIsLike(String id, int no, String userId){
        return contLikeDAO.existsByIdAndNoAndUserId(id, no, userId);
    }

    /**
     * O(contIds * contNo * userId)
     */
    public void saveContLikeToDB() {
        try{
            //1 移動到batch後再慢慢處理
            redisTemplate.rename(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH);

            //2 取出資料
            Set<String> values = redisTemplate.opsForSet().members(keyUtil.LIKE_BATCH);
            if(values.isEmpty()){
                log.info("No data to save contLike");
                return;
            }

            //3 找出需更新的資料, 整理每個content增加或減少了多少likes, 先查出來處理一次, 這樣deleteAll就不會再select
            List<ContLike> createList = new ArrayList<>();//透過lua script, create跟delete不可能同時存在
            List<ContLike> deleteList = new ArrayList<>();
            Map<String, Map<Integer, Integer>> createCount = new HashMap<>();//<id, <no, count>>
            Map<String, Map<Integer, Integer>> deleteCount = new HashMap<>();
            List<String[]> dataList = values.stream().map((v) -> v.split(":")).collect(Collectors.toList());
            for(String[] data : dataList){
                String id = data[0];
                int no = Integer.parseInt(data[1]);
                String userId = data[2];
                int likeStatus = Integer.parseInt(data[3]);

                ContLike contLike = contLikeDAO.findByIdAndNoAndUserId(id, no, userId);
                if(likeStatus == LIKE && contLike == null){
                    contLike = new ContLike(id, no, userId);
                    createList.add(contLike);

                    if(!createCount.containsKey(id))
                        createCount.put(id, new HashMap<>());

                    Map<Integer, Integer> noMap = createCount.get(id);
                    noMap.put(no, noMap.getOrDefault(no, 0) + 1);
                }else if(likeStatus == UNLIKE && contLike != null){
                    deleteList.add(contLike);

                    if(!deleteCount.containsKey(id))
                        deleteCount.put(id, new HashMap<>());

                    Map<Integer, Integer> noMap = deleteCount.get(id);
                    noMap.put(no, noMap.getOrDefault(no, 0) + 1);
                }
            }

            //4 更新contLike事務
            txnService.saveContLikeToDBTxn(createList, deleteList);

            //5 更新成功後刪除redis資料
            redisTemplate.delete(keyUtil.LIKE_BATCH);
            redisTemplate.delete(keyUtil.LIKE_STATIC);

            //6 更新content事務
            txnService.updateContentLikesTxn(createCount, deleteCount);

            log.info("Save contLike succeeded, create {} likes in {} contents, delete {} likes in {} contents",
                    createList.size(), createCount.size(), deleteList.size(), deleteCount.size());
        } catch (Exception e) {
            log.error("Save ContLike to DB failed:", e);
            throw new RuntimeException(e);
        }
    }



}