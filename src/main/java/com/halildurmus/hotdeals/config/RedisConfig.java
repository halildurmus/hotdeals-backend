package com.halildurmus.hotdeals.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisConfig {

//  @Bean
//  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//    final RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//        .disableCachingNullValues()
//        .entryTtl(Duration.ofMinutes(10));
//
//    return RedisCacheManager
//        .builder(connectionFactory)
//        .cacheDefaults(cacheConfiguration)
//        .build();
//  }

}
