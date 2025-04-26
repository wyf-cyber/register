package com.itheima.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class RedisHealthCheck {
    private static final Logger logger = LoggerFactory.getLogger(RedisHealthCheck.class);
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @EventListener(ApplicationReadyEvent.class)
    public void checkRedisHealth() {
        try {
            // 尝试连接Redis
            RedisConnection connection = redisConnectionFactory.getConnection();
            connection.close();
            logger.info("Redis connection successful");
        } catch (Exception e) {
            logger.warn("Redis connection failed, attempting to start Redis server...");
            startRedisServer();
        }
    }

    private void startRedisServer() {
        try {
            // 检查Redis是否已经运行
            if (isRedisRunning()) {
                logger.info("Redis is already running");
                return;
            }

            // 获取Redis安装路径("D:\\Redis\\Redis-x64-5.0.14.1\\redis-server.exe")
            String redisPath = "D:\\Redis\\Redis-x64-5.0.14.1\\redis-server.exe";
            
            // 启动Redis服务器
            ProcessBuilder processBuilder = new ProcessBuilder(redisPath);
            Process process = processBuilder.start();
            
            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("Redis server output: {}", line);
            }
            
            // 等待Redis启动
            Thread.sleep(2000);
            
            // 再次检查Redis连接
            RedisConnection connection = redisConnectionFactory.getConnection();
            connection.close();
            logger.info("Redis server started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start Redis server: {}", e.getMessage());
        }
    }

    private boolean isRedisRunning() {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("redis-server.exe")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking Redis process: {}", e.getMessage());
            return false;
        }
    }
} 