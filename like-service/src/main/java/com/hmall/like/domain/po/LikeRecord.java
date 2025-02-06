package com.hmall.like.domain.po;

import groovy.transform.builder.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeRecord {
    private Long id;
    private Long userId;
    private Long materialId;
    private Boolean type; // 0代表视频，1代表评论
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDelete;

}
