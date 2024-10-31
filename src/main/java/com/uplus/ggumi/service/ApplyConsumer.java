package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplyConsumer {

    private static final String STREAM_KEY = "apply_stream";
    private static final String GROUP_NAME = "apply_group";
    private static final String CONSUMER_NAME = "apply_consumer";

    private final ApplyRepository applyRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();

        // 스트림이 존재하지 않으면 첫 메시지를 넣어 스트림을 생성
        if (Boolean.FALSE.equals(redisTemplate.hasKey(STREAM_KEY))) {
            Map<String, String> initMessage = new HashMap<>();
            initMessage.put("message", "init");
            streamOps.add(STREAM_KEY, initMessage);
        }

        // 그룹이 존재하지 않으면 그룹을 생성
        try {
            streamOps.createGroup(STREAM_KEY, GROUP_NAME);
        } catch (Exception e) {
            // 그룹이 이미 존재하면 예외 무시
        }
    }

    @Scheduled(fixedDelay = 1000)  // 2초 간격으로 실행
    public void pollMessages() {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();

        List<MapRecord<String, Object, Object>> messages = streamOps.read(
                Consumer.from(GROUP_NAME, CONSUMER_NAME),
                StreamReadOptions.empty().block(Duration.ofSeconds(1)),
                StreamOffset.create("apply_stream", ReadOffset.lastConsumed())
        );

        if (messages != null) {
            messages.forEach(this::processMessage);
        } else {
            log.info("No new messages in stream.");
        }
    }

    private void processMessage(MapRecord<String, Object, Object> record) {
        ApplyRequestDto requestDto = mapToDto(record.getValue());

        CompletableFuture.runAsync(() -> {
            Apply applyEntity = new Apply(
                    requestDto.getName(),
                    requestDto.getPhoneNumber(),
                    requestDto.getApplyTime()
            );
            applyRepository.save(applyEntity);

            // 처리 완료 후 Ack를 통해 메시지 확인 처리
            redisTemplate.opsForStream().acknowledge(GROUP_NAME, record);
        }).exceptionally(e -> {
            log.error("Error saving message to MySQL", e);
            return null;
        });
    }

    private ApplyRequestDto mapToDto(Map<Object, Object> value) {
        return new ApplyRequestDto(
                (String) value.get("name"),
                (String) value.get("phoneNumber"),
                Long.parseLong(value.get("applyTime").toString())
        );
    }
}
