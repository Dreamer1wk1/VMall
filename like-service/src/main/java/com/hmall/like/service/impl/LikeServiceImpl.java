package com.hmall.like.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.hmall.common.utils.UserContext;
import com.hmall.like.service.LikeRedisService;
import com.hmall.like.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Resource
    private LikeRedisService likeRedisService;
    @Resource
    private Snowflake snowflake;

    @Override
    public void like(Long materialId, Boolean type) {
//        Long userId = UserContext.getUser();
        Long userId = snowflake.nextId();
        materialId = snowflake.nextId();
//        log.info("用户{}点赞{}", userId, materialId);
        likeRedisService.Like(userId, materialId, type);
    }

    @Override
    public void unlike(Long materialId, Boolean type) {
        Long userId = UserContext.getUser();
        likeRedisService.unlike(userId, materialId, type);
    }

    @Override
    public Boolean isLike(Long materialId, Boolean type) {
        Long userId = UserContext.getUser();
        return likeRedisService.isLike(userId, materialId, type);
    }

    @Override
    public Integer getCount(Long materialId, Boolean type) {
        return null;
    }

}
