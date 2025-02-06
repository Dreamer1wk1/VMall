package com.hmall.comment.domain.vo;

import com.hmall.comment.domain.po.Index;
import lombok.Data;

import java.util.List;

@Data
public class CommentVO {
    Index root;
    List<Index> children;
}
