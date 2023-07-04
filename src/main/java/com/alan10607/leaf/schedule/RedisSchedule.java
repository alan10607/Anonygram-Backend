package com.alan10607.leaf.schedule;

import com.alan10607.leaf.service.ContLikeServiceOld;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@AllArgsConstructor
@Slf4j
public class RedisSchedule extends QuartzJobBean {

    private ContLikeServiceOld contLikeServiceOld;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            contLikeServiceOld.saveContLikeToDB();
            log.info("Schedule saveContLikeToDB succeeded");
        } catch (Exception e) {
            log.error("Schedule saveContLikeToDB fail");
            throw new JobExecutionException(e);
        }
    }

}