package com.uplus.ggumi.service;

import com.uplus.ggumi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String TOTAL_LIKES = "totalLikes:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final BookRepository bookRepository;

    /* 30분마다 메서드 실행 */
    @Scheduled(fixedRate = 1800000)
    public void persistLikesToRDB() {
        log.info("총 좋아요 데이터 Redis -> MySQL 영구 저장");
        Set<String> keys = redisTemplate.keys(TOTAL_LIKES + "*");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Long bookId = Long.parseLong(key.split(":")[1]);
                Object cachedLikes = redisTemplate.opsForValue().get(TOTAL_LIKES + bookId);

                if (cachedLikes != null) {
                    bookRepository.updateLikes(bookId, Integer.parseInt(cachedLikes.toString()));
                    redisTemplate.delete(TOTAL_LIKES + bookId);
                }
            }
        }
    }
}
