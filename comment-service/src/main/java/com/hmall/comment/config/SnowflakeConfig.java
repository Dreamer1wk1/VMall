package com.hmall.comment.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfig {
    @Bean
    public Snowflake snowflake() {
        // 参数1为终端ID，参数2为数据中心ID
        return IdUtil.getSnowflake(2, 2);
    }
}
