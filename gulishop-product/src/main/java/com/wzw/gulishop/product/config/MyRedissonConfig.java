package com.wzw.gulishop.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @auther Kevin
 * @ClassName MyRedissonConfig
 * @Date 2021.07.14 1:14
 */


@Configuration
public class MyRedissonConfig {


    /**
     * 所有对Redis的使用是通过ReddissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
   public RedissonClient redisson() throws IOException {
        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");

//        根据Config创建出RedissonClient示例
        RedissonClient redissonClient = Redisson.create(config);

        return redissonClient;
    }


}
