package com.alan10607.ag.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Slf4j
public class RedisConfig {
    public String host;
    public String port;
    public String password;

//    @Bean
//    public LettuceConnectionFactory connectionFactory(){
//        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
//        conf.setHostName("localhost");
//        conf.setPort(6379);
//        return new LettuceConnectionFactory(conf);
//    }

    /**
     * Set redis connection instance, Spring Boots uses Lettuce by default
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Long> redisTemplate(LettuceConnectionFactory connectionFactory){
        log.info("RedisTemplate config start, connectionFactory hostName={}, port={}",
                connectionFactory.getHostName(),
                connectionFactory.getPort());

        RedisTemplate<String, Long> template = new RedisTemplate<>();

        //Set the connection factory, LettuceConnectionFactory setting is set in application.yaml
        template.setConnectionFactory(connectionFactory);

        /*
        StringRedisSerializer: general strings
        JdkSerializationRedisSerializer: used by default, the object needs to implement Serializable
        Jackson2JsonRedisSerializer: Store in json format, specify the deserialization class when initializing
        GenericJackson2JsonRedisSerializer: Stored in json format, less efficient, when storing in redis, an additional @class hashmap will be stored as a deserialized class
         */
        //Set the serialization method
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        //Enable Transaction, the default is disabled
        //template.setEnableTransactionSupport(true);

        //set these parameters
        template.afterPropertiesSet();

        log.info("RedisTemplate config succeeded");
        return template;
    }

    /**
     * Set and create RedissonClient
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        String address = String.format("redis://%s:%s", host, port);
        log.info("RedissonConfig config address={}", address);
        Config config = new Config();
        config.useSingleServer().setAddress(address).setPassword(password);
        RedissonClient redissonClient = Redisson.create(config);
        log.info("RedissonConfig config succeeded");
        return redissonClient;
    }

    @Bean
    public DefaultRedisScript<Long> setContentLikeScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/set_content_like.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<Long> isMemberMultiScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/is_member_multi.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

}