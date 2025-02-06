package com.hmall.data.domain.po;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class canalDTO {
    private JSONArray data;       // 更新后的数据
    private String database;      // 数据库名
    private JSONArray es;              // 数据的主键 ID
    private JSONArray old;        // 更新前的数据（仅在 UPDATE 时有效）
    private String table;         // 表名
    private Long ts;              // 时间戳
    private String type;          // 操作类型（INSERT, UPDATE, DELETE）
}
