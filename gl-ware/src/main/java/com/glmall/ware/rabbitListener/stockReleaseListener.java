package com.glmall.ware.rabbitListener;

import TO.WareOrderTaskTo;
import com.glmall.ware.service.StockService;
import com.glmall.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class stockReleaseListener {

    @Autowired
    StockService stockService;

    @RabbitHandler
    public void stockReleaseHandler(WareOrderTaskTo wareOrderTaskTo, Channel channel, Message message) throws IOException {

        try {
            stockService.unLockStock(wareOrderTaskTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    @RabbitHandler public void orderClosedHandler(OrderVo orderVo,Channel channel,Message message) throws IOException {
        try {
            stockService.unLockStock(orderVo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
