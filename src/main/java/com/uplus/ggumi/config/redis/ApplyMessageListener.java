package com.uplus.ggumi.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyMessageListener implements MessageListener {

    private static final String APPLY_COUNT_KEY = "apply:count";
    private static final long MAX_WINNERS = 100;

    private final ApplyRepository applyRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            /* Redis에서 메시지를 역직렬화 */
            String messageBody = new String(message.getBody());
            ApplyRequestDto requestDto = objectMapper.readValue(messageBody, ApplyRequestDto.class);

            /* 1. 중복 응모 체크 */
            String phoneNumberKey = "apply:phone:" + requestDto.getPhoneNumber();
            Boolean isExist = redisTemplate.hasKey(phoneNumberKey);

            if (Boolean.TRUE.equals(isExist)) {
                log.info("중복 응모 발견 : {}", requestDto.getPhoneNumber());
                return;
            }

            /* 2. 원자적으로 카운터 증가 및 현재 카운트 확인
            * INCR 명령은 Redis에서 원자적으로 처리되므로 정확히 1명만 선발하는 데 유용 */
            Long currentCount = redisTemplate.opsForValue().increment(APPLY_COUNT_KEY);

            if (currentCount > MAX_WINNERS) {
                redisTemplate.opsForValue().decrement(APPLY_COUNT_KEY);
                return;
            }

            /* 3. 중복 방지를 위해 전화번호 키 저장 (TTL 설정) */
            redisTemplate.opsForValue().set(phoneNumberKey, "1");
            redisTemplate.expire(phoneNumberKey, 24 * 60 * 60, TimeUnit.SECONDS);

            /* 4. DB에 응모 정보 저장 */
            saveToDatabase(requestDto);

        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 오류 발생 : ", e);
        }
    }

    private void saveToDatabase(ApplyRequestDto requestDto) {
        try {
            Apply apply = Apply.builder()
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .build();

            applyRepository.save(apply);
        } catch (Exception e) {
            log.error("DB 저장 중 오류 발생 : {}", e.getMessage());
            throw new ApiException(ErrorCode.APPLY_FAILED);
        }
    }
}
