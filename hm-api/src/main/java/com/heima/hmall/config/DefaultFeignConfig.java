package com.heima.hmall.config;

import com.heima.hmall.client.TradeService;
import com.heima.hmall.fallback.*;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    //注册关于商品调用的fallback
    @Bean
    public ItemClientFallback itemClientFallback(){
        return new ItemClientFallback();
    }
    @Bean
    public CartClientFallback cartClientFallback(){
        return new CartClientFallback();
    }
    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }
    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }
    @Bean
    public PayClientFallback payClientFallback(){
        return new PayClientFallback();
    }

    //定义feign请求拦截器，设置用户信息
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }
    //配置feign的日志级别
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
