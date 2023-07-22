package com.alan10607.ag.service;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dao.ContentDAO;
import com.alan10607.redis.service.IdRedisService;
import com.alan10607.redis.service.IdStrRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class IdService  {
    private final ContentDAO contentDAO;
    private final IdRedisService idRedisService;
    private final IdStrRedisService idStrRedisService;

    public List<String> get() {
        if(Strings.isBlank(idStrRedisService.get())) {
            pullStringToRedis();
        }
        return Arrays.asList(idStrRedisService.get().split(","));
    }

    private void pullStringToRedis() {
        if(idRedisService.get().isEmpty()){
            pullToRedis();
        }
        List<String> idList = idRedisService.get();
        String idStr = String.join(",", idList);
        idStrRedisService.set(idStr);
        log.info("Set idStr to redis succeed, id size={}", idList.size());
    }

    private void pullToRedis() {
        List<String> sortedIdList = contentDAO.findLatest100Id(StatusType.NORMAL.name());
        idRedisService.set(sortedIdList);
        log.info("Set id to redis succeed, id size={}", sortedIdList.size());
    }

    public void set(String id) {
        idRedisService.set(id);
        idStrRedisService.delete();
    }
}