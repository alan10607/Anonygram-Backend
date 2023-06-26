package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleServiceNew;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.impl.ArticleRedisService;
import com.alan10607.redis.service.impl.IdRedisService;
import com.alan10607.redis.service.impl.IdStrRedisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
public class IdService  {
    private final ArticleDAO articleDAO;
    private final IdRedisService idRedisService;
    private final IdStrRedisService idStrRedisService;

    public String get(String id) {
        if(!idStrRedisService.hasKey()) {
            pullStringToRedis();
        }
        return idStrRedisService.get();
    }

    private void pullStringToRedis() {
        if(!idRedisService.hasKey()){
            pullToRedis();
        }
        List<String> idList = idRedisService.get();
        String idStr = idList.stream().collect(Collectors.joining(","));
        idStrRedisService.set(idStr);
        log.info("Set idStr to redis succeed, id size={}", idList.size());
    }

    private void pullToRedis() {
        List<String> sortedIdList = articleDAO.findLatest100Id(StatusType.NEW.name());
        idRedisService.set(sortedIdList);
        log.info("Set id to redis succeed, id size={}", sortedIdList.size());
    }

    @AfterDeleteRedis
    public void create(String id, LocalDateTime updateTime) {
        idRedisService.set(id, updateTime);
    }

    @AfterDeleteRedis
    public void updateScore(String id, LocalDateTime updateTime) {
        idRedisService.updateScore(id, updateTime);
    }


}