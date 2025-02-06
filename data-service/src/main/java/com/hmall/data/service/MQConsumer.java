package com.hmall.data.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hmall.data.domain.po.ElasticsearchOperation;
import com.hmall.data.domain.po.canalDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hmall.common.utils.MQConstants.ITEM_TOPIC;

@Service
@Slf4j
@RocketMQMessageListener(
        topic = ITEM_TOPIC,
        consumerGroup = "${rocketmq.consumer.dataSync.group}",
        messageModel = MessageModel.CLUSTERING
)
public class MQConsumer implements RocketMQListener<String> {

    @Autowired
    private ESService esService;

    @Override
    public void onMessage(String message) {
        log.info("Received RocketMQ message: {}", message);
        try {
            // 解析消息为 canalDTO
            canalDTO dto = parseCanalMessage(message);
            // 处理消息类型，INSERT、UPDATE、DELETE
            if ("INSERT".equals(dto.getType())) {
                esService.batchProcessOperation(ElasticsearchOperation.builder()
                        .table(dto.getTable())
                        .type(dto.getType())
                        .data(dto.getData())
                        .build());
            } else if ("UPDATE".equals(dto.getType())) {
                esService.batchProcessOperation(ElasticsearchOperation.builder()
                        .table(dto.getTable())
                        .type(dto.getType())
                        .old(dto.getOld())
                        .data(dto.getData())
                        .build());
            } else if ("DELETE".equals(dto.getType())) {
                esService.batchProcessOperation(ElasticsearchOperation.builder()
                        .table(dto.getTable())
                        .type(dto.getType())
                        .id(dto.getEs())
                        .build());
            }
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    // 解析 Canal 消息为 canalDTO
    private canalDTO parseCanalMessage(String message) {
        JSONObject jsonMessage = JSONObject.parseObject(message);

        canalDTO dto = new canalDTO();
        dto.setDatabase(jsonMessage.getString("database"));
        dto.setTable(jsonMessage.getString("table"));
        dto.setTs(jsonMessage.getLong("ts"));
        dto.setType(jsonMessage.getString("type"));
        dto.setEs(jsonMessage.getJSONArray("es"));

        // 处理更新操作中的数据
        if ("UPDATE".equals(dto.getType())) {
            // 解析 old 和 data 数据
            dto.setOld(jsonMessage.getJSONArray("old"));
        }
        dto.setData(jsonMessage.getJSONArray("data"));

        return dto;
    }
}
