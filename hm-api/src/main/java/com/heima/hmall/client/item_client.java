package com.heima.hmall.client;

import com.heima.hmall.config.DefaultFeignConfig;
import com.heima.hmall.dto.ItemDTO;
import com.heima.hmall.dto.OrderDetailDTO;
import com.heima.hmall.fallback.ItemClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

//标注是一个feign客户端，然后指定了客户名称，这样可以获取到该微服务的服务实例列表，
//并基于负载均衡获取一个服务实例,第二个参数是指定日志级别文件
@FeignClient(value = "item-service",fallbackFactory = ItemClientFallback.class,configuration = DefaultFeignConfig.class)
public interface item_client {
    @GetMapping("/items")
    public List<ItemDTO> queryItemByIds(@RequestParam("ids")Collection<Long> ids);
    @PutMapping("/items/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);
    //根据商品id获取dto
    @GetMapping("/items/{id}")
    public ItemDTO queryItemById(@PathVariable("id") Long itemId);
}
