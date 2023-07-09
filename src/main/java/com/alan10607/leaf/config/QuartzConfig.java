package com.alan10607.leaf.config;

import com.alan10607.leaf.schedule.SaveLikeSchedule;
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
    public JobDetail redisJobDetail(){
        return JobBuilder.newJob(SaveLikeSchedule.class)
                    .storeDurably()//"storeDurably" means persisting tasks
                    .build();
    }

    /**
     * Bind task's details
     * @return
     */
    @Bean
    public Trigger redisTrigger(){
        ScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 0/20 * * * ?"); //Start from 0 and execute every 20 minutes

        return TriggerBuilder.newTrigger()
                .forJob(redisJobDetail())
                .withSchedule(scheduleBuilder)
                .build();
    }

}
