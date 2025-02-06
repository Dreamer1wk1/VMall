package com.hmall.like.domain.po;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedisRecord {
    private Long userId;
    private Long materialId;
    private Boolean type; // 0代表视频，1代表评论

    public RedisRecord(Long userId, Long materialId, Boolean type) {
        this.userId = userId;
        this.materialId = materialId;
        this.type = type;
    }
}
