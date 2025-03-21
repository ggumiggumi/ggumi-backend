package com.uplus.ggumi.service;

import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.repository.ApplyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final static String APPLY_STREAM = "apply:stream";
    private final static String CONSUMER_GROUP = "apply-processors";
    private final static String CONSUMER_NAME = "apply-db-server";
    private final ApplyRepository applyRepository;
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        try {
            /* Stream이 없으면 생성 */
            createStreamIfNotExists();

            /* Consumer 그룹이 없으면 생성 */
            createConsumerGroupIfNotExists();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void createStreamIfNotExists() {
        Boolean isExists = redisTemplate.hasKey(APPLY_STREAM);
        if (Boolean.FALSE.equals(isExists)) {
            Map<String, String> initialMessage = Map.of("init", "true");
            redisTemplate.opsForStream().add(APPLY_STREAM, initialMessage);

            log.info("Created Stream : {}", APPLY_STREAM);
        }
    }

    private void createConsumerGroupIfNotExists() {
        try {
            redisTemplate.opsForStream().createGroup(APPLY_STREAM, CONSUMER_GROUP);
            log.info("Created Consumer Group : {}", CONSUMER_GROUP);
        } catch (Exception e) {
            log.info("Consumer Group Creation Failed");
        }
    }

    @Override
    @Transactional
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            Map<String, String> values = message.getValue();

            String name = values.get("name");
            String phoneNumber = values.get("phoneNumber");
            Long applyTime = Long.parseLong(values.get("applyTime"));

            Apply apply = Apply.builder()
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .applyTime(applyTime)
                    .build();

            applyRepository.save(apply);

            RecordId messageId = message.getId();
            redisTemplate.opsForStream().acknowledge(APPLY_STREAM, CONSUMER_GROUP, messageId.getValue());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
