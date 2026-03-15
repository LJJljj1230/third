package com.hmall.search.listener;

import com.hmall.common.constants.MqConstants;
import com.hmall.search.service.ISearchService;
import com.sun.jdi.Value;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class itemStatusListener {
    @Autowired
    private ISearchService iSearchService;
    //监听商品上架的消息，将商品保存到es中
    @RabbitListener(bindings = @QueueBinding(
            //队列
            value = @Queue(value = "search.item.up.queue", durable = "true"),
            //交换机
            exchange =@Exchange(value = MqConstants.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            //路由键
            key = MqConstants.ITEM_UP_KEY
    ))
    public void itemUp(Long itemId){
        iSearchService.saveItemByid( itemId);

    }




    //监听商品下架的消息，想es中对应的该商品删除
    @RabbitListener(bindings = @QueueBinding(
            //队列
            value = @Queue(value = "search.item.down.queue", durable = "true"),
            //交换机
            exchange =@Exchange(value = MqConstants.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            //路由键
            key = MqConstants.ITEM_DOWN_KEY
    ))
    public void downUp(Long itemId){
        iSearchService.deleteItemByid(itemId);

    }

}
