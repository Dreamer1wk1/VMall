package com.hmall.data.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.hmall.common.utils.MQConstants.ORDER_CANCEL_LEVEL;
import static com.hmall.common.utils.MQConstants.ORDER_CANCEL_TOPIC;

@Slf4j
@Service
public class MQProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void scheduled(Long id) {
        Message<Long> rocketMessage = MessageBuilder.withPayload(id).build();
        SendResult sendResult = rocketMQTemplate.syncSend(ORDER_CANCEL_TOPIC, rocketMessage, 1000, ORDER_CANCEL_LEVEL);

    }
}
