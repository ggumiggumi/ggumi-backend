package com.uplus.ggumi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    /* Redis Stream 키 값으로, 메시지를 저장하고 소비할 Redis Stream 이름 */
    private static final String APPLY_STREAM = "apply_stream";
    /* Stream에 대한 Consumer Group의 이름으로 메시지를 여러 Consumer가 그룹으로 처리할 수 있도록 관리 */
    private static final String APPLY_GROUP = "apply_group";
    /* Consumer Group 내의 특정 Consumer의 이름으로 각 Consumer가 그룹 내에서 메시지를 읽어서 처리할 수 있도록 함 */
    private static final String APPLY_CONSUMER = "apply_consumer";

    private final ApplyRepository applyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /* Spring에서 Bean이 생성된 후 실행되는 메서드로 초기화를 위함 */
    @PostConstruct
    public void init() {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();

        // 스트림이 존재하지 않으면 첫 메시지를 넣어 스트림을 생성
        if (Boolean.FALSE.equals(redisTemplate.hasKey(APPLY_STREAM))) {
            Map<String, String> initMessage = new HashMap<>();
            initMessage.put("message", "init");
            streamOps.add(APPLY_STREAM, initMessage);
        }

        // 그룹이 존재하지 않으면 그룹을 생성
        try {
            streamOps.createGroup(APPLY_STREAM, APPLY_GROUP);
        } catch (Exception e) {
            // 그룹이 이미 존재하면 예외 무시
        }
    }

    /* 10초 간격으로 pollMessages() 메서드 실행 */
    @Scheduled(fixedDelay = 10000)
    public void pollMessages() {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();

        /* read() 메서드를 통해 APPLY_GROUP 의 APPLY_CONSUMER 라는 Consumer를 통해 메시지를 읽어온다. */
        List<MapRecord<String, Object, Object>> messages = streamOps.read(
                Consumer.from(APPLY_GROUP, APPLY_CONSUMER),
                /* Redis Stream을 최대 0.1초 동안 블로킹해서 메시지를 기다린다. 0.1초 안에 새로운 메시지가 있으면 즉시 읽고, 없으면 0.1초 후 반환한다. */
                StreamReadOptions.empty().block(Duration.ofMillis(100)),
                /* Consumer Group의 마지막으로 읽은 메시지 이후의 메시지를 읽어온다. */
                StreamOffset.create(APPLY_STREAM, ReadOffset.lastConsumed())
        );

        /* 읽어온 메시지가 있으면 processMessage() 메서드 실행 */
        if (messages != null) {
            messages.forEach(this::processMessage);
        }

    }

    /* 메시지 처리 및 MySQL로 저장하기 위한 수행 메서드 */
    private void processMessage(MapRecord<String, Object, Object> record) {
        ApplyRequestDto requestDto = objectMapper.convertValue(record.getValue(), ApplyRequestDto.class);

        /* runAsync()를 통해 비동기적으로 메시지를 처리하며 MySQL 데이터베이스에 저장한다. */
        CompletableFuture.runAsync(() -> {
            applyRepository.save(new Apply(
                    requestDto.getName(),
                    requestDto.getPhoneNumber(),
                    requestDto.getApplyTime()
            ));

            /* 처리 완료 후 Ack를 통해 메시지 확인 처리
            * 메시지 처리가 완료되면 acknowledge()를 호출해 해당 Consumer Group에서 메시지가 처리되었음을 Redis에 알려준다.
            * 이를 통해 동일한 메시지를 다시 읽어오는 일이 발생하지 않도록 해준다. */
            redisTemplate.opsForStream().acknowledge(APPLY_GROUP, record);
        }).exceptionally(e -> {
            log.error("Error saving message to MySQL", e);
            return null;
        });
    }
}
