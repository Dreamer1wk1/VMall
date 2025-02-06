package com.hmall.like.service;

import cn.hutool.core.lang.Snowflake;
import com.hmall.like.domain.po.LikeRecord;
import com.hmall.like.domain.po.RedisRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeDBService {
    @Resource
    private Snowflake snowflake;
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void insertLikeRecords(List<LikeRecord> records) {
        String sql = "insert into like_record(id, user_id, material_id, type, create_time, update_time, is_delete) values(?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = records.stream()
                .map(record -> new Object[]{
                        record.getId(),
                        record.getUserId(),
                        record.getMaterialId(),
                        record.getType(),
                        record.getCreateTime(),
                        record.getUpdateTime(),
                        record.getIsDelete()
                })
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
