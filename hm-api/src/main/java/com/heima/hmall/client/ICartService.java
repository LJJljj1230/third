package com.heima.hmall.client;

import com.heima.hmall.config.DefaultFeignConfig;
import com.heima.hmall.fallback.CartClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "cart-service",fallbackFactory = CartClientFallback.class,configuration = DefaultFeignConfig.class)
public interface ICartService {
    @DeleteMapping("/carts")
    public void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);
    }


