package com.hmall.comment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmall.comment.domain.dto.CommentDTO;
import com.hmall.comment.domain.po.Content;
import com.hmall.comment.domain.po.Index;
import com.hmall.comment.domain.vo.CommentVO;
import com.hmall.comment.mapper.ContentMapper;
import com.hmall.comment.mapper.IndexMapper;
import com.hmall.comment.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    final int MAX_LENGTH = 40;
    @Resource
    private Snowflake snowflake;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private IndexMapper indexMapper;


    // 自定义线程池（可以根据实际情况调整线程数）
    private ExecutorService executor = Executors.newFixedThreadPool(20);
    @Override
    @Transactional
    public void saveComment(CommentDTO commentDTO) {
        Index index = new Index();
        Content content = new Content();
        // id
        index.setId(snowflake.nextId());
        content.setCommentId(index.getId());
        index.setRoot(commentDTO.getRoot());
        index.setParent(commentDTO.getParent());
        String contentStr = commentDTO.getContent();
        // index的摘要
        if (contentStr.length() > MAX_LENGTH) {
            index.setSummary(contentStr.substring(0, MAX_LENGTH));
            index.setIsFull(false);
        } else {
            index.setSummary(contentStr);
            index.setIsFull(true);
        }
        content.setContent(contentStr);
        // 点赞数
        index.setLike(0);

        // 保存
        indexMapper.insert(index);
        contentMapper.insert(content);
    }

    // 获取视频的评论(同步)
    @Override
    public List<CommentVO> getById(Long materialId, Integer page, Integer size) {
        // 分页查询根评论
        PageHelper.startPage(page, size);
        List<Index> rootIndexes = indexMapper.getRoot(materialId);
        // 构造结果列表
        List<CommentVO> commentVOS = new ArrayList<>();
        // 遍历根评论
        for (Index root : rootIndexes) {
            List<Index> children = indexMapper.getByParentLimit1(root.getId());

            // 组装 VO 对象
            CommentVO vo = new CommentVO();
            vo.setRoot(root);
            vo.setChildren(children);
            commentVOS.add(vo);
        }

        return commentVOS;
    }


    @Override
    public List<Index> getMore(Long commentId, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Index> indexes = indexMapper.getByParent(commentId);
        PageInfo<Index> pageInfo = new PageInfo<>(indexes);
        return pageInfo.getList();
    }


    @Override
    public Content getFull(Long commentId) {
        return contentMapper.getById(commentId);
    }

    /**
     * 异步查询首页根评论和其对应最高赞的子评论
     */
    @Override
    public CompletableFuture<List<CommentVO>> queryRootCommentsWithBestChild(Long materialId, Integer page, Integer size) {
        // 1. 异步查询根评论
        CompletableFuture<List<Index>> rootFuture = CompletableFuture.supplyAsync(() -> {
            PageHelper.startPage(page, size);
            return indexMapper.getRoot(materialId);
        }, executor);

        // 2. 对每个根评论异步查询对应的最高赞子评论
        CompletableFuture<List<CommentVO>> resultFuture = rootFuture.thenCompose(rootComments -> {
            List<CompletableFuture<CommentVO>> voFutures = rootComments.stream()
                    .map(rootComment -> {
                        return CompletableFuture.supplyAsync(() -> {
                            // 查询每个根评论下点赞数最高的子评论
                            List<Index> bestChild = indexMapper.getByParentLimit1(rootComment.getId());
                            CommentVO vo = new CommentVO();
                            vo.setRoot(rootComment);
                            vo.setChildren(bestChild);
                            return vo;
                        }, executor);
                    })
                    .collect(Collectors.toList());

            // 等待所有子评论查询完成，组合成结果列表
            CompletableFuture<Void> allDone = CompletableFuture.allOf(
                    voFutures.toArray(new CompletableFuture[0])
            );
            return allDone.thenApply(v -> voFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
        });

        return resultFuture;
    }
}
