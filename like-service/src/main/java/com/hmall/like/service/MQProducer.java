package com.hmall.like.service;

import com.hmall.like.domain.po.LikeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.hmall.common.utils.MQConstants.RECORD_TOPIC;

@Slf4j
@Service
public class MQProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void send(List<LikeRecord> likeRecords) {
        Message<List<LikeRecord> > rocketMessage = MessageBuilder.withPayload(likeRecords).build();
        SendResult sendResult = rocketMQTemplate.syncSend(RECORD_TOPIC, rocketMessage, 10000);
//        log.info("点赞记录已发送：" + sendResult.getSendStatus().toString());
    }

}
