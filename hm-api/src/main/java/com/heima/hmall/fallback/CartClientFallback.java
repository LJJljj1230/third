package com.heima.hmall.fallback;

import com.heima.hmall.client.ICartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;

@Slf4j
public class CartClientFallback implements FallbackFactory<ICartService> {
    @Override
    public ICartService create(Throwable cause) {
        return new ICartService() {
            @Override
            public void deleteCartItemByIds(Collection<Long> ids) {
                log.error("远程调用CartClient.deleteCartItemByIds 失败；ids = {}", ids, cause);
            }
        };
    }
}