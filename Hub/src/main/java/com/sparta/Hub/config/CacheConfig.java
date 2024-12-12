package com.sparta.Hub.config;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching //어노테이션을 붙이므로 우리는 메서드에 붙일 수 있는 어노테이션이 늘어난다.-> service단을 확인해보자
public class CacheConfig {


  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory redisConnectionFactory
  ) {
    RedisCacheConfiguration configuration=RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .entryTtl(Duration.ofSeconds(10))
        .computePrefixWith(CacheKeyPrefix.simple())
        .serializeValuesWith(
            SerializationPair.fromSerializer(RedisSerializer.json())
        );

    return RedisCacheManager
        .builder(redisConnectionFactory)
        .cacheDefaults(configuration)
        .build();

  }
}
