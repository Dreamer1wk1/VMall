package com.hmall.data.es;

import com.alibaba.fastjson.JSONObject;
import com.hmall.data.domain.po.ElasticsearchOperation;
import com.hmall.data.domain.po.canalDTO;
import com.hmall.data.service.ESService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SyncTest {
    @Autowired
    private ESService esService;
    @Test
    void testSync(){
        // 模拟 CanalDTO 对象
        String jsonMessage = "{\"data\":{\"id\":1,\"name\":\"Test Item\",\"price\":99.99,\"category\":\"Electronics\",\"stock\":50},\"database\":\"hmall-item\",\"es\":null,\"old\":null,\"table\":\"item_test\",\"ts\":1691152000000,\"type\":\"INSERT\"}";
        canalDTO dto = parseCanalMessage(jsonMessage);
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
    }
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
