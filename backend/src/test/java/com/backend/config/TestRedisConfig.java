package com.backend.config;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@Profile("test")
public class TestRedisConfig {

    private RedisServer redisServer;
    private int redisPort;

    @PostConstruct
    public void startRedis() throws IOException {
        redisPort = findAvailableTcpPort();
        redisServer = new RedisServer(redisPort);
        redisServer.start();

        // Spring Boot Redis 설정에 반영
        System.setProperty("spring.data.redis.host", "localhost");
        System.setProperty("spring.data.redis.port", String.valueOf(redisPort));

        System.out.printf("Embedded Redis 서버 시작 (port: %d)%n", redisPort);
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            System.out.println("Embedded Redis 서버 종료");
        }
    }

    private int findAvailableTcpPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }
}