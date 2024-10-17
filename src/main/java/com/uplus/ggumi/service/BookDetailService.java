package com.uplus.ggumi.service;

import com.uplus.ggumi.repository.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookDetailService implements RedisService {

    public static final String LIKE = "like:";
    public static final String HATE = "hate:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long setLike(Integer key, Integer value) {
        /* 싫어요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());

        return redisTemplate.opsForSet().add(LIKE + key, value.toString());
    }

    @Override
    public Long setHate(Integer key, Integer value) {
        /* 좋아요에 대한 기록을 지워줌 */
        redisTemplate.opsForSet().remove(LIKE + key.toString(), value.toString());

        return redisTemplate.opsForSet().add(HATE + key, value.toString());
    }

    @Override
    public Long undoFeedback(Integer key, Integer value) {
        /* 좋아요/싫어요 모든 정보를 지워줌 */
        redisTemplate.opsForSet().remove(HATE + key.toString(), value.toString());
        return redisTemplate.opsForSet().remove(LIKE + key, value.toString());

    }
}
