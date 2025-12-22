package com.heima.hmall.client;

import com.heima.hmall.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "trade-service",configuration = DefaultFeignConfig.class)
public interface TradeService {
    @PutMapping("/carts/{orderId}")
    public void updateById(@PathVariable("orderId") Long orderId) ;

}
