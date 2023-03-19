package com.alan10607.leaf.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
@Slf4j
public class RedissonConfig {
    public String host;
    public String port;
    public String address;
    public String password;

    /**
     * 設定並建立 RedissonClient
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        this.address = String.format("redis://%s:%s", host, port);
        log.info("RedissonConfig config address={}", address);
        Config config = new Config();
        config.useSingleServer().setAddress(address).setPassword(password);
        RedissonClient redissonClient = Redisson.create(config);
        log.info("RedissonConfig config succeeded");
        return redissonClient;
    }

}