package com.uplus.ggumi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import com.uplus.ggumi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String TOTAL_LIKES = "totalLikes:";
    public static final String APPLY_QUEUE = "applyQueue";
    public static final String APPLY_SET = "applySet";

    private final RedisTemplate<String, Object> redisTemplate;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final ApplyRepository applyRepository;

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

    /* 주기적으로 Redis 큐에서 데이터를 가져와 MySQL에 배치로 저장 */
    @Scheduled(fixedRate = 30000)
    public void saveMySQLFromRedis() {
        List<Apply> applyList = new ArrayList<>();

        while (true) {
            /* Redis 큐에서 데이터 가져오기 */
            String applyJson = (String) redisTemplate.opsForList().leftPop(APPLY_QUEUE);

            /* Queue가 비어 있으면 작업 중지 */
            if (applyJson == null) {
                break;
            }

            try {
                ApplyRequestDto requestDto = objectMapper.readValue(applyJson, ApplyRequestDto.class);
                Boolean isMember = redisTemplate.opsForSet().isMember(APPLY_SET, requestDto.getPhoneNumber());

                if (Boolean.TRUE.equals(isMember)) {
                    continue;
                }

                applyList.add(Apply.builder()
                        .name(requestDto.getName())
                        .phoneNumber(requestDto.getPhoneNumber())
                        .applyTime(requestDto.getApplyTime())
                        .build());

                redisTemplate.opsForSet().add(APPLY_SET, requestDto.getPhoneNumber());

            } catch (Exception e) {
                log.error("데이터 변환 중 오류 발생 : ", e);
            }
        }

        /* MySQL에 배치 저장 */
        if (!applyList.isEmpty()) {
            applyRepository.saveAll(applyList);
        } else {
            log.info("Redis 큐에 저장할 데이터가 없습니다.");
        }
    }
}
