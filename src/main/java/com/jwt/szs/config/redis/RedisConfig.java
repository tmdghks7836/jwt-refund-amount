package com.jwt.szs.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() throws IOException {

        try {

            redisServer = RedisServer.builder()
                    .port(redisPort)
                    .build();
            log.info("redis start : port {} isActive = {} ", redisPort, redisServer.isActive());
            redisServer.start();
        }catch (Exception e){
            log.error("해당 포트가 프로세스로 실행되고 있으면 에러가 남. 임시로 try catch");
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}