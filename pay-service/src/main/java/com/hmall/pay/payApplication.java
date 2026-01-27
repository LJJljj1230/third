package com.hmall.pay;

import com.heima.hmall.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(value = "com.heima.hmall.client",defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.hmall.pay.mapper")
@SpringBootApplication
public class payApplication {
    public static void main(String[] args) {
        SpringApplication.run(payApplication.class, args);
    }
}
