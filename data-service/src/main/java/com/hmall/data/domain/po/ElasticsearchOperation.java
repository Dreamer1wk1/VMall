package com.hmall.data.domain.po;

import com.alibaba.fastjson.JSONArray;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticsearchOperation {
    private String type;    // 操作类型：INSERT、UPDATE、DELETE
    private String table;   // 表名
    private JSONArray id;        // 数据的主键 ID（如果是 DELETE 或 UPDATE 时需要）
    private JSONArray data; // 数据内容，针对 INSERT 和 UPDATE 来说（更新后的数据）
    private JSONArray old;  // 更新前的数据，仅在 UPDATE 操作时有效
}
