package com.heima.hmall.fallback;


import com.heima.hmall.client.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;

@Slf4j
public class TradeClientFallback implements FallbackFactory<TradeService> {
    @Override
    public TradeService create(Throwable cause) {
        return new TradeService() {
            @Override
            public void markOrderPaySuccess(Long orderId) {
                log.error("远程调用TradeClient.markOrderPaySuccess 失败；参数{}", orderId, cause);
            }
        };
    }
}