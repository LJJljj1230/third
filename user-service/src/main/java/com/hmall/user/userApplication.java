package com.hmall.user;

import com.heima.hmall.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(value = "com.heima.hmall.client",defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.hmall.user.mapper")
@SpringBootApplication
public class userApplication {
    public static void main(String[] args) {
        SpringApplication.run(userApplication.class, args);
    }
}
