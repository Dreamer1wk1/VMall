package com.hmall.comment.controller;


import com.hmall.comment.domain.dto.CommentDTO;
import com.hmall.comment.domain.po.Content;
import com.hmall.comment.domain.po.Index;
import com.hmall.comment.domain.vo.CommentVO;
import com.hmall.comment.service.CommentService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Api(tags = "评论相关接口")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    @Resource
    private CommentService commentService;
    @PostMapping("/save")
    public void saveComment(@RequestBody CommentDTO commentDTO) {
        commentService.saveComment(commentDTO);
    }
    // 获取视频的评论
    @GetMapping("/getById")
    public List<CommentVO> getById(@RequestParam("materialId") Long materialId,
                                   @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return commentService.getById(materialId,page,size);
    }
    @GetMapping("/getByIdAsync")
    public CompletableFuture<List<CommentVO>> getByIdAsync(
            @RequestParam("materialId") Long materialId, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return commentService.queryRootCommentsWithBestChild(materialId,page,size);
    }
    // 获取根评论的更多子评论
    @GetMapping("/getMore")
    public List<Index> getMore(@RequestParam("commentId") Long commentId,
                               @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return commentService.getMore(commentId,page,size);
    }
    @GetMapping("/getFull")
    public Content getFull(@RequestParam("commentId") Long commentId) {
        return commentService.getFull(commentId);
    }
}
