package com.alan10607.ag.schedule;

import com.alan10607.ag.service.forum.LikeBatchService;
import com.alan10607.ag.service.forum.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@AllArgsConstructor
@Slf4j
public class SaveLikeSchedule extends QuartzJobBean {

    private LikeBatchService likeBatchService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            likeBatchService.startBatch();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

}