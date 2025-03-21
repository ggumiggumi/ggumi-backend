package com.uplus.ggumi.service;

import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private static final String APPLY_COUNT_KEY = "apply:count";
    private static final String APPLY_PHONE_PREFIX = "apply:phone";
    private static final String APPLY_STREAM = "apply:stream";
    private static final int MAX_APPLICANTS = 100;
    private final ApplyRepository applyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /* 1단계 기본 Spring Boot + MySQL 을 사용한 단일 모듈 구조 */
    @Transactional
    public String apply(ApplyRequestDto requestDto) {

        /* 중복 응모 체크 */
        if (applyRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new ApiException(ErrorCode.DUPLICATE_APPLY);
        }

        /* 현재 응모자 수 확인 */
        long count = applyRepository.count();
        if (count >= 100) {
            throw new ApiException(ErrorCode.APPLY_LIMIT_EXCEEDED);
        }

        try {
            /* 서버에서 현재 시간 측정 */
            long currentServerTime = System.currentTimeMillis();

            /* 응모 데이터 저장 */
            Apply apply = Apply.builder()
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .applyTime(currentServerTime)
                    .build();

            applyRepository.save(apply);
            return "SUCCESS";
        } catch (Exception e) {
            throw new ApiException(ErrorCode.APPLY_FAILED);
        }
    }

    @Transactional
    public String applyWithStream(ApplyRequestDto requestDto) {
        String phoneNumber = requestDto.getPhoneNumber();
        String phoneKey = APPLY_PHONE_PREFIX + phoneNumber;

        /* 1. Redis를 사용한 중복 응모 체크 */
        Boolean isFirstApply = redisTemplate.opsForValue().setIfAbsent(phoneKey, "applied");
        if (Boolean.TRUE.equals(isFirstApply)) {
            throw new ApiException(ErrorCode.DUPLICATE_APPLY);
        }

        /* 2. Redis를 사용한 응모자 수 증가 */
        Long currentApplicants = redisTemplate.opsForValue().increment(APPLY_COUNT_KEY);

        /* 3. 최대 응모자 수 초과 확인 */
        if (currentApplicants > MAX_APPLICANTS) {
            throw new ApiException(ErrorCode.APPLY_LIMIT_EXCEEDED);
        }

        /* 4. 서버 시간 측정 */
        long currentServerTime = System.currentTimeMillis();

        /* 5. Redis Stream에 응모 정보 저장 */
        Map<String, String> applyInfo = new HashMap<>();
        applyInfo.put("name", requestDto.getName());
        applyInfo.put("phoneNumber", phoneNumber);
        applyInfo.put("applyTime", String.valueOf(currentServerTime));

        /* Stream에 데이터 추가 */
        stringRedisTemplate.opsForStream().add(
                APPLY_STREAM,
                applyInfo
        );

        return "SUCCESS";
    }

}
