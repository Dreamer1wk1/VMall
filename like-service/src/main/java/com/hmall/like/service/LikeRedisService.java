package com.hmall.like.service;

import cn.hutool.core.lang.Snowflake;
import com.hmall.common.utils.UserContext;
import com.hmall.like.domain.po.LikeRecord;
import com.hmall.like.domain.po.RedisRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hmall.like.domain.constants.BATCH_SIZE;
import static com.hmall.like.domain.constants.RECORD_KEY;

@Service
@Slf4j
public class LikeRedisService {
    @Resource
    private MQProducer mqProducer;
    @Resource
    private Snowflake snowflake;
    @Resource
    private LikeDBService likeDBService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void Like(Long userId, Long materialId, Boolean type) {
        String likeKey = userId + ":" + materialId + ":" + type;
        stringRedisTemplate.opsForSet().add(RECORD_KEY, likeKey);
    }

    public void unlike(Long userId, Long materialId, Boolean type) {
        String likeKey = userId + ":" + materialId + ":" + type;
        stringRedisTemplate.opsForSet().remove(RECORD_KEY, likeKey);
    }

    public Boolean isLike(Long userId, Long materialId, Boolean type) {
        String likeKey = userId + ":" + materialId + ":" + type;
        return stringRedisTemplate.opsForSet().isMember(RECORD_KEY, likeKey);
    }

    public List<RedisRecord> getCache() {
        Set<String> records = stringRedisTemplate.opsForSet().members(RECORD_KEY);

        if (records == null || records.isEmpty()) {
            return List.of();
        }

        // 解析字符串，转换成 RedisRecord 对象
        return records.stream()
                .map(this::parseRecord)
                .collect(Collectors.toList());
    }

    private RedisRecord parseRecord(String record) {
        String[] parts = record.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid record format: " + record);
        }
        return new RedisRecord(
                Long.parseLong(parts[0]), // userId
                Long.parseLong(parts[1]), // materialId
                Boolean.parseBoolean(parts[2]) // type
        );
    }

    @Scheduled(fixedDelay = 5000) // 每5秒执行一次
    public void syncLikesToMQ() {
        // 从缓存中获取数据
        List<RedisRecord> cacheRecord = getCache();
        // 清空缓存
        stringRedisTemplate.delete(RECORD_KEY);
        if (cacheRecord.isEmpty()) {
            return;
        }
        log.info("开始同步点赞记录到MQ");
        // 转换后分批发送到MQ
        List<LikeRecord> likeRecords = convert(cacheRecord);
        int totalRecords = likeRecords.size();
        for (int i = 0; i < totalRecords; i += BATCH_SIZE) {
            // 分批获取子列表
            List<LikeRecord> batchList = likeRecords.subList(i, Math.min(i + BATCH_SIZE, totalRecords));
            // 发送到MQ
//            mqProducer.send(batchList);
            likeDBService.insertLikeRecords(batchList);
        }
    }

    // 转换
    public List<LikeRecord> convert(List<RedisRecord> cacheRecords) {
        List<LikeRecord> likeRecords = new ArrayList<>();
        for (RedisRecord cacheRecord : cacheRecords) {
            LikeRecord likeRecord = new LikeRecord();
            likeRecord.setId(snowflake.nextId());
            likeRecord.setUserId(cacheRecord.getUserId());
            likeRecord.setMaterialId(cacheRecord.getMaterialId());
            likeRecord.setType(cacheRecord.getType());
            likeRecord.setIsDelete(false);
            likeRecords.add(likeRecord);
        }
        return likeRecords;
    }


}
