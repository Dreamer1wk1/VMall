package com.hmall.like.controller;

import com.hmall.like.service.LikeService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(tags = "点赞相关接口")
@RequestMapping("/like")
public class LikeController {
    @Resource
    private LikeService likeService;

    @GetMapping("/like")
    public void like(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type) {
        likeService.like(materialId, type); // 记录点赞
        // TODO
//        likeService.incrCount(materialId, type); // 增加点赞数
    }

    @GetMapping("/unlike")
    public void unlike(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type) {
        // 先判断redis中是否存在
        if (likeService.isLike(materialId, type)) {
            likeService.unlike(materialId, type);
        }
    }

    @GetMapping("/isLike")
    public Boolean isLike(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type) {
        return likeService.isLike(materialId, type);
    }

    public Integer getCount() {
        return 0;
    }
}
