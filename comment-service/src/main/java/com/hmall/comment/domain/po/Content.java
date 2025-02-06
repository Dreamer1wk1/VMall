package com.hmall.comment.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("index")
public class Content {
    private Long commentId;
    private String content;
}
