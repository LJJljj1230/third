package com.heima.hmall.client;

import com.heima.hmall.config.DefaultFeignConfig;
import com.heima.hmall.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service",fallbackFactory = UserClientFallback.class,configuration = DefaultFeignConfig.class)
public interface UserService {
    @PutMapping("/users/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
