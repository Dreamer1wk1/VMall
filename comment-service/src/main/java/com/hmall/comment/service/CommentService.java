package com.hmall.comment.service;

import com.hmall.comment.domain.dto.CommentDTO;
import com.hmall.comment.domain.po.Content;
import com.hmall.comment.domain.po.Index;
import com.hmall.comment.domain.vo.CommentVO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommentService {
    void saveComment(CommentDTO commentDTO);

    List<CommentVO> getById(Long materialId, Integer page, Integer size);

    List<Index> getMore(Long commentId, Integer page, Integer size);

    Content getFull(Long commentId);

    CompletableFuture<List<CommentVO>> queryRootCommentsWithBestChild(Long materialId, Integer page, Integer size);
}
