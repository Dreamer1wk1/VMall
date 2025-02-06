package com.hmall.comment.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "评论DTO")
public class CommentDTO {
    private Boolean root;
    private Long parent;
    private String content;
}
