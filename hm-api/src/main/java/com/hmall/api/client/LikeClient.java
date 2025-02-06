package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("like-service")
public interface LikeClient {
    @GetMapping("/like")
    void like(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type);
    @GetMapping("/unlike")
    void unlike(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type);
    @GetMapping("/isLike")
    Boolean isLike(@RequestParam("materialId") Long materialId, @RequestParam("type") Boolean type);
}
