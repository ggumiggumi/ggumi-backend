package com.uplus.ggumi.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplySubscriber implements MessageListener {

    private final ApplyRepository applyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            /* Redis 에서 수신한 JSON 메시지를 ApplyRequestDto로 변환 */
            ApplyRequestDto requestDto = objectMapper.readValue(new String(message.getBody()), ApplyRequestDto.class);

            /* MySQL 저장 */
            applyRepository.save(Apply.builder()
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .applyTime(requestDto.getApplyTime())
                    .build());

        } catch (Exception e) {
            log.error("Redis 구독 중 오류 발생 : ", e);
        }
    }
}
