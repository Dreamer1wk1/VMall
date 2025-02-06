package com.hmall.like.service;

import com.hmall.like.domain.po.LikeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmall.common.utils.MQConstants.RECORD_TOPIC;

@Service
@Slf4j
@RocketMQMessageListener(
        topic = RECORD_TOPIC,
        consumerGroup = "${rocketmq.consumer.likeRecord.group}",
        messageModel = MessageModel.CLUSTERING
)
public class MQConsumer implements RocketMQListener<List<LikeRecord>> {
    @Resource
    LikeDBService likeDBService;
    @Override
    public void onMessage(List<LikeRecord> records) {
//        log.info("接收到消息，开始插入数据库");
        likeDBService.insertLikeRecords(records);
//        log.info("点赞记录插入数据库成功");
    }
}