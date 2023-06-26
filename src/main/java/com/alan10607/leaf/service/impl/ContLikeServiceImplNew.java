package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.TxnService;
import com.alan10607.leaf.util.RedisKeyUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeServiceImplNew implements ContLikeService {
    private TxnService txnService;
    private ContLikeDAO contLikeDAO;
    private final RedisTemplate redisTemplate;
    private final DefaultRedisScript checkLikeScript;
    private final DefaultRedisScript toggleLikeScript;
    private final RedisKeyUtil keyUtil;
    private static final int LIKE = 1;
    private static final int UNLIKE = 0;

    /**
     * 查詢是否已經有這個userId的按讚紀錄
     * 順序: LIKE_NEW > LIKE_BATCH > LIKE_STATIC > DB
     * LIKE_STATIC: 只能由DB放入資料, 唯讀, 禁止別的方法修改
     * LIKE_BATCH: 若剛好在跑批會用這個替代LIKE_NEW, 批次結束後刪除, 新資料從DB重新查詢後放入LIKE_STATIC
     * LIKE_NEW: 有任何異動優先修改這個, 查詢也優先以這個為主, 批次開始時刪除
     * @param id
     * @param no
     * @param userId
     * @return
     */
    public boolean findContLikeFromRedis(String id, int no, String userId){
        String isLike = keyUtil.LikeValue(id, no, userId, LIKE);
        String unLike = keyUtil.LikeValue(id, no, userId, UNLIKE);

        //順序: LIKE_NEW > LIKE_BATCH > LIKE_STATIC > DB
        Long isSuccess = (Long) redisTemplate.execute(checkLikeScript,
                Arrays.asList(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH, keyUtil.LIKE_STATIC),
                isLike, unLike);

        //redis查不到, 到DB查詢
        if(isSuccess == -1){
            if(findIsLike(id, no, userId)) {
                redisTemplate.opsForSet().add(keyUtil.LIKE_STATIC, isLike);
                return true;
            }else {
                redisTemplate.opsForSet().add(keyUtil.LIKE_STATIC, unLike);
                return false;
            }
        }

        return isSuccess == 1;//isSuccess = 1 or 0
    }

    /**
     * 按讚, 若已按讚會跳過, 透過lua腳本實現原子操作
     * @param id
     * @param no
     * @param userId
     * @return
     * @throws Exception
     */
    public boolean UpdateIsLikeFromRedis(String id, int no, String userId) {
        Long isSuccess = (Long) redisTemplate.execute(toggleLikeScript,
                Arrays.asList(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH, keyUtil.LIKE_STATIC),
                keyUtil.LikeValue(id, no, userId, LIKE),
                keyUtil.LikeValue(id, no, userId, UNLIKE));

        if(isSuccess == 0){
            log.error("Already like, skip this time, id={}, no={}, userId={}", id, no, userId);
        }else if(isSuccess == -1) {
            throw new RuntimeException(
                    String.format("Update like by lua failed because set not found, isSuccess=-1, id=%s, no=%s, userId=%s", id, no, userId));
        }

        return isSuccess == 1;
    }

    /**
     * 取消讚, 若已取消會跳過, 透過lua腳本實現原子操作
     * @param id
     * @param no
     * @param userId
     * @return
     * @throws Exception
     */
    public boolean UpdateUnLikeFromRedis(String id, int no, String userId) {
        Long isSuccess = (Long) redisTemplate.execute(toggleLikeScript,
                Arrays.asList(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH, keyUtil.LIKE_STATIC),
                keyUtil.LikeValue(id, no, userId, UNLIKE),
                keyUtil.LikeValue(id, no, userId, LIKE));

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
     * 透過批次將按讚資料存入DB, 除了更新ContLike的按讚資料外, 一併更新Content的likes按讚次數
     * 時間需要 O(contId size * contNo size * userId size)
     * 若批次失敗可以設計成保留資料後重新跑批
     */
    public void saveContLikeToDB() {
        try{
            //1 移動到batch後再慢慢處理
            if(!redisTemplate.hasKey(keyUtil.LIKE_NEW)) {
                log.info("No data to save contLike");
                return;
            }
            redisTemplate.rename(keyUtil.LIKE_NEW, keyUtil.LIKE_BATCH);

            //2 取出資料
            Set<String> values = redisTemplate.opsForSet().members(keyUtil.LIKE_BATCH);

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

            //7 成功後依序刪除cont緩存
            for(Map.Entry<String, Map<Integer, Integer>> id : createCount.entrySet()){
                for(Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                    redisTemplate.delete(keyUtil.cont(id.getKey(), no.getKey()));
            }
            for(Map.Entry<String, Map<Integer, Integer>> id : deleteCount.entrySet()) {
                for (Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                    redisTemplate.delete(keyUtil.cont(id.getKey(), no.getKey()));
            }

            log.info("Save contLike succeeded, create {} likes in {} contents, delete {} likes in {} contents",
                    createList.size(), createCount.size(), deleteList.size(), deleteCount.size());
        } catch (Exception e) {
            log.error("Save ContLike to DB failed:", e);
            throw new RuntimeException(e);
        }
    }

}
