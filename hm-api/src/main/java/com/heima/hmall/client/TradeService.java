package com.heima.hmall.client;

import com.heima.hmall.config.DefaultFeignConfig;
import com.heima.hmall.fallback.TradeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "trade-service",fallbackFactory = TradeClientFallback.class,configuration = DefaultFeignConfig.class)
public interface TradeService {
    @PutMapping("/orders/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) ;

}
