package com.halildurmus.hotdeals.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisConfig {

//  @Bean
//  public RedisCacheConfiguration cacheConfiguration() {
//    return RedisCacheConfiguration.defaultCacheConfig()
//        .disableCachingNullValues()
//        .entryTtl(Duration.ofMinutes(10));
//  }

}
