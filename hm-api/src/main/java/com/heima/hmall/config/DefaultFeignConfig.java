package com.heima.hmall.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;



public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogger(){
        return Logger.Level.FULL;
    }
}
