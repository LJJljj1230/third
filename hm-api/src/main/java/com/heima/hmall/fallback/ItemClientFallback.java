package com.heima.hmall.fallback;

import com.heima.hmall.client.item_client;
import com.heima.hmall.dto.ItemDTO;

import com.heima.hmall.dto.OrderDetailDTO;

import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ItemClientFallback implements FallbackFactory<item_client> {
    @Override
    public item_client create(Throwable cause) {
        return new item_client() {
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("远程调用ItemClient.queryItemByIds方法出现异常；参数{}", ids, cause);
                //查询购物车列表允许失败，返回空集合
                return CollUtils.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                //库存扣减失败，需要触发事务回滚；所以这里抛出异常
                throw new BizIllegalException(cause);
            }
        };
    }
}