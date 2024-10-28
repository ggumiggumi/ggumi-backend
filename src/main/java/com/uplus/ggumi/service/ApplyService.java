package com.uplus.ggumi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    public static final String APPLY = "apply";
    public static final String APPLY_CHANNEL = "applyChannel";
    public static final String APPLY_QUEUE = "applyQueue";
    private final ApplyRepository applyRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, ApplyRequestDto> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /* 1단계 기본 Spring Boot + MySQL을 사용한 단일 모듈 구조
     * 응모 요청이 들어오면 MySQL에 바로 save()를 호출해 데이터 저장 */
    public String applyVer1(ApplyRequestDto requestDto) {

        if (applyRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) return "FAILED";

        applyRepository.save(Apply.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .applyTime(requestDto.getApplyTime())
                .build());

        return "SUCCESS";
    }

    /* 2단계 Spring Boot + Redis + MySQL을 사용한 단일 모듈 구조
     * 응모 요청이 들어오면 Redis SET 응모를 받으며 중복 검사를 동시에 진행함.
     * Redis Pub/Sub 기능 추가
     * */
    public String applyVer2(ApplyRequestDto requestDto) {

        String phoneNumber = requestDto.getPhoneNumber();

        /* 중복 확인 */
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(APPLY, phoneNumber))) {
            return "FAILED";
        }

        redisTemplate.opsForSet().add(APPLY, phoneNumber);

        try {
            /* ApplyRequestDto 객체를 JSON으로 변환하여 Redis 채널에 발행하며
            *  Pub/Sub 채널로 메시지를 발행한다.  */
            redisTemplate.convertAndSend(APPLY_CHANNEL, objectMapper.writeValueAsString(requestDto));

        } catch (Exception e) {
            log.error("응모 발행 중 오류 발생 : ", e);
            return "FAILED";
        }
        return "SUCCESS";
    }

    public String applyVer4(ApplyRequestDto requestDto) {
        try {
            kafkaTemplate.send("apply", requestDto.getPhoneNumber(), requestDto);
        } catch (Exception e) {
            log.error("응모 발행 중 오류 발생 : ", e);
            return "FAILED";
        }
        return "SUCCESS";
    }

    /* 클라이언트 요청 시 Redis 큐에 저장 */
    public String applyVer3(ApplyRequestDto requestDto) {
        try {
            String applyJson = objectMapper.writeValueAsString(requestDto);
            redisTemplate.opsForList().rightPush(APPLY_QUEUE, applyJson);
        } catch (Exception e) {
            log.error("Redis 큐에 저장 중 오류 발생 : ", e);
            return "FAILED";
        }
        return "SUCCESS";
    }


}
