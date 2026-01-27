package com.hmall.cart;

import com.heima.hmall.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//开启openfeigh功能，可以扫描到那些客户端
@EnableFeignClients(value = "com.heima.hmall.client",defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.hmall.cart.mapper")
@SpringBootApplication
public class cartApplication {
    public static void main(String[] args) {
        SpringApplication.run(cartApplication.class, args);
    }
}
