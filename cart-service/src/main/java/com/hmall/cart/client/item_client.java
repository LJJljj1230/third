package com.hmall.cart.client;

import com.hmall.cart.domain.dto.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

//标注是一个feign客户端，然后指定了客户名称，这样可以获取到该微服务的服务实例列表，
//并基于负载均衡获取一个服务实例
@FeignClient("item-service")
public interface item_client {
    @GetMapping("/items")
    public List<ItemDTO> queryItemByIds(@RequestParam("ids")Collection<Long> ids);
}
