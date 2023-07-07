package com.alan10607.leaf.schedule;

import com.alan10607.leaf.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@AllArgsConstructor
@Slf4j
public class SaveLikeSchedule extends QuartzJobBean {

    private LikeService likeService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            likeService.saveLikeToDB();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

}