package com.hmall.like;

import com.hmall.like.domain.po.LikeRecord;
import com.hmall.like.service.LikeRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootTest
public class ShardingTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LikeRedisService likeRedisService;

    // 插入点赞记录
    @Test
    public void insertLikeRecordTest() {
        for (int i = 1; i <= 10; i++) {  // 插入10条数据
            long likeId = generateId(); // 生成唯一ID（可用Snowflake算法）
            long targetId = 1000 + i;
            String sql = "INSERT INTO like_record (id, user_id, material_id, type) VALUES (?, 1, 1, 1)";
            jdbcTemplate.update(sql, likeId, (long) i, targetId);
            System.out.println("Inserted like_record: id=" + likeId + ", userId=" + (long) i);
        }
    }

    // 分页查询测试
    @Test
    public void queryLikeRecordTest() {
        List<LikeRecord> records = queryPage(0, 5);
        for (LikeRecord record : records) {
            System.out.println(record);
        }
    }
    @Test
    public void scheduleTest() {
        likeRedisService.syncLikesToMQ();
    }

    public List<LikeRecord> queryPage(int offset, int limit) {
        String sql = "SELECT * FROM like_record ORDER BY id LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{limit, offset}, new BeanPropertyRowMapper<>(LikeRecord.class));
    }

    // 生成全局唯一ID（模拟Snowflake算法）
    private long generateId() {
        return System.currentTimeMillis(); // 实际使用 Snowflake 或其他ID生成算法
    }
}

