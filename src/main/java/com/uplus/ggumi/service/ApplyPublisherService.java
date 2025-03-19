package com.uplus.ggumi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.config.exception.ApiException;
import com.uplus.ggumi.config.exception.ErrorCode;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyPublisherService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic applyTopic;
    private final ObjectMapper objectMapper;

    /* 응모 요청을 Redis에 발행 */
    public String publishApply(ApplyRequestDto requestDto) {
        try {
            /* 유효성 검사 */
            if (requestDto.getPhoneNumber() == null || requestDto.getName() == null) {
                throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
            }

            /* 요청 객체를 JSON 문자열로 직렬화 */
            String message = objectMapper.writeValueAsString(requestDto);

            /* Redis 토픽에 메시지 발행 */
            redisTemplate.convertAndSend(applyTopic.getTopic(), message);

            return "SUCCESS";

        } catch (JsonProcessingException e) {
            log.error("응모 요청 직렬화 실패 : ", e);
            throw new ApiException(ErrorCode.APPLY_FAILED);
        } catch (Exception e) {
            log.error("응모 요청 발행 중 오류 발생 : ", e);
            throw new ApiException(ErrorCode.APPLY_FAILED);
        }
    }
}
