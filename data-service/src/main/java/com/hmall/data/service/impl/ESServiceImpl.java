package com.hmall.data.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hmall.data.domain.po.ElasticsearchOperation;
import com.hmall.data.service.ESService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
public class ESServiceImpl implements ESService {
    private final RestHighLevelClient client;

    @Autowired
    public ESServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void batchProcessOperation(ElasticsearchOperation operation) {
        BulkRequest bulkRequest = new BulkRequest();

        switch (operation.getType()) {
            case "INSERT":
                // 处理批量插入
                if (operation.getData() != null && !operation.getData().isEmpty()) {
                    for (Object record : operation.getData()) {
                        JSONObject recordJson = (JSONObject) record;
                        String id = recordJson.getString("id");
                        bulkRequest.add(new IndexRequest(operation.getTable())
                                .id(id) // 使用 ID 作为文档 ID
                                .source(recordJson.toString(), XContentType.JSON));
                    }
                }
                break;

            case "UPDATE":
                // 处理批量更新
                if (operation.getData() != null && !operation.getData().isEmpty()) {
                    for (Object record : operation.getData()) {
                        JSONObject recordJson = (JSONObject) record;
                        String id = recordJson.getString("id");
                        bulkRequest.add(new UpdateRequest(operation.getTable(), id)
                                .doc(recordJson.toString(), XContentType.JSON) // 更新文档
                                .upsert(recordJson.toString(), XContentType.JSON)); // 如果文档不存在则插入
                    }
                }
                break;

            case "DELETE":
                // 处理批量删除
                if (operation.getId() != null && !operation.getId().isEmpty()) {
                    for (Object id : operation.getId()) {
                        bulkRequest.add(new DeleteRequest(operation.getTable(), id.toString()));
                    }
                }
                break;

            default:
                log.warn("Unsupported operation type: {}", operation.getType());
                break;
        }

        try {
            // 执行批量操作
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

            // 检查是否有失败项
            if (bulkResponse.hasFailures()) {
                log.error("Batch operation failed: {}", bulkResponse.buildFailureMessage());
            } else {
                log.info("Batch operation completed successfully.");
            }
        } catch (IOException e) {
            log.error("Error occurred while processing batch operation: ", e);
        }
    }
}
