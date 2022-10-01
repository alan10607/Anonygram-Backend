package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.util.RedisKeyUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeServiceImpl implements ContLikeService {
    private ContentDAO contentDAO;
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
        boolean isSuccess = false;
        RLock lock = redisson.getLock(keyUtil.likeLock(id, no, userId));
        try{
            lock.lock();
            if(!findContLikeFromRedis(id, no, userId)) {
                redisTemplate.opsForSet().add(keyUtil.LIKE_NEW, isLike);
                redisTemplate.opsForSet().remove(keyUtil.LIKE_NEW, unLike);
                isSuccess = true;
            }else{
                log.error("Already like, skip this time, id={}, no={}, userId={}", id, no, userId);
            }
        } catch (Exception e) {
            log.error("Update contLike to isLike failed, id={}, no={}, userId={}", id, no, userId, e);
            throw new Exception(e);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return isSuccess;
    }

    public boolean UpdateUnLikeFromRedis(String id, int no, String userId) throws Exception {
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        // 指定 lua 脚本
//        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/DelKey.lua")));
//        // 指定返回类型
//        redisScript.setResultType(Long.class);
//        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
//        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey),UUID);
//        System.out.println(result);

        String isLike = keyUtil.LikeValue(id, no, userId, LIKE);
        String unLike = keyUtil.LikeValue(id, no, userId, UNLIKE);
        boolean isSuccess = false;
        RLock lock = redisson.getLock(keyUtil.likeLock(id, no, userId));
        try{
            lock.lock();
            if(findContLikeFromRedis(id, no, userId)) {
                redisTemplate.opsForSet().add(keyUtil.LIKE_NEW, unLike);
                redisTemplate.opsForSet().remove(keyUtil.LIKE_NEW, isLike);
                isSuccess = true;
            }else{
                log.error("Already unlike, skip this time, id={}, no={}, userId={}", id, no, userId);
            }
        } catch (Exception e) {
            log.error("Update contLike to unLike failed, id={}, no={}, userId={}", id, no, userId, e);
            throw new Exception(e);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return isSuccess;
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

            //3 找出需更新的資料, 整理每個content增加或減少了多少likes, 避免saveAll, deleteAll重新select Entity所以將處理寫在@Transactional?????????????????
            List<ContLike> createList = new ArrayList<>();//create跟delete不可能同時存在
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
                    contLike.setId(id);
                    contLike.setNo(no);
                    contLike.setUserId(userId);
                    createList.add(contLike);

                    if(!createCount.containsKey(id))
                        createCount.put(id, new HashMap<>());

                    Map<Integer, Integer> noMap = createCount.get(id);
                    noMap.put(no, noMap.getOrDefault(no, 0));
                }else if(likeStatus == UNLIKE && contLike != null){
                    deleteList.add(contLike);

                    if(!deleteCount.containsKey(id))
                        deleteCount.put(id, new HashMap<>());

                    Map<Integer, Integer> noMap = deleteCount.get(id);
                    noMap.put(no, noMap.getOrDefault(no, 0));
                }
            }

            //4 更新contLike事務
            saveContLikeToDBTxn(createList, deleteList);

            //5 更新成功後刪除redis資料
            redisTemplate.delete(keyUtil.LIKE_BATCH);
            redisTemplate.delete(keyUtil.LIKE_STATIC);

            //6 更新content事務
            updateContentLikesTxn(createCount, deleteCount);

            log.info("Save contLike succeeded, create {} likes in {} contents, delete {} likes in {} contents",
                    createList.size(), createCount.size(), deleteList.size(), deleteCount.size());
        } catch (Exception e) {
            log.error("Save ContLike to DB failed:", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void saveContLikeToDBTxn(List<ContLike> createList, List<ContLike> deleteList){
        contLikeDAO.saveAll(createList);
        contLikeDAO.deleteAllInBatch(deleteList);
    }

    @Transactional
    public void updateContentLikesTxn(Map<String, Map<Integer, Integer>> createMap, Map<String, Map<Integer, Integer>> deleteMap){
        for(Map.Entry<String, Map<Integer, Integer>> id : createMap.entrySet()){
            for(Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                contentDAO.incrLikes(id.getKey(), no.getKey(), no.getValue());
        }

        for(Map.Entry<String, Map<Integer, Integer>> id : deleteMap.entrySet()){
            for(Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                contentDAO.incrLikes(id.getKey(), no.getKey(), ( - no.getValue()));
        }
    }

}