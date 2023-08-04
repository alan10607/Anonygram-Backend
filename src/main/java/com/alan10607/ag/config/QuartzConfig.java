package com.alan10607.ag.config;

import com.alan10607.ag.schedule.SaveLikeSchedule;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    /**
     * Bind specific the task
     * @return
     */
    @Bean
    public JobDetail saveLikeJobDetail(){
        return JobBuilder.newJob(SaveLikeSchedule.class)
                    .storeDurably()//"storeDurably" means persisting tasks
                    .build();
    }

    /**
     * Bind task's details
     * @return
     */
    @Bean
    public Trigger saveLikeTrigger(){
        ScheduleBuilder<CronTrigger> scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 0/20 * * * ?"); //Start from 0 and execute every 20 minutes

        return TriggerBuilder.newTrigger()
                .forJob(saveLikeJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }

}