package com.heima.hmall.fallback;


import com.heima.hmall.client.PayClient;
import com.heima.hmall.dto.PayOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class PayClientFallback implements FallbackFactory<PayClient> {
    public PayClient create(Throwable cause) {
        return new PayClient() {
            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long id) {
                log.error("远程调用PayClient.queryPayOrderByBizOrderNo 失败；参数{}", id, cause);
                return null;
            }
        };
    }
}