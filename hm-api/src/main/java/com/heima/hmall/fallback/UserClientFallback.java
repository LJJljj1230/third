package com.heima.hmall.fallback;

import com.heima.hmall.client.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class UserClientFallback implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable cause) {
        return new UserService() {
            @Override
            public void deductMoney(String pw, Integer amount) {
                log.error("远程调用UserClient.deductMoney 失败；参数pw={},amount={}", pw, amount, cause);
            }
        };
    }
}