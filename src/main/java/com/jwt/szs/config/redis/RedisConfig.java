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
            log.info("redis start : port {} ", redisPort);
            redisServer.start();
            //TODO 해결방안 찾아야함
        }catch (Exception e){
            log.error("전체 테스트 진행 시 테스트 하나마다 redis server 가 살아있음에도 계속 시작 되어 에러가 남. 임시로 try catch");
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