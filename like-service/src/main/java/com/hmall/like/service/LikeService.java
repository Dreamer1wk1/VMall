package com.hmall.like.service;

public interface LikeService {
    public void like(Long materialId,Boolean type);
    public void unlike(Long materialId,Boolean type);
    public Integer getCount(Long materialId, Boolean type);
    public Boolean isLike(Long materialId, Boolean type);

}
