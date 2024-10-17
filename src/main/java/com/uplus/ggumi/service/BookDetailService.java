package com.uplus.ggumi.service;

import com.uplus.ggumi.repository.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookDetailService implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public static final String LIKE = "like:";
    public static final String HATE = "hate:";

    @Override
    public Long setLike(Integer key, Integer value) {
        return redisTemplate.opsForSet().add(LIKE + key.toString(), value.toString());
    }

    @Override
    public Long setHate(Integer key, Integer value) {
        return redisTemplate.opsForSet().add(HATE + key.toString(), value.toString());
    }
}
