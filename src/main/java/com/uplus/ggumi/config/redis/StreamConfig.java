package com.uplus.ggumi.config.redis;

import com.uplus.ggumi.service.ApplyStreamListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class StreamConfig {

    private static final String APPLY_STREAM = "apply:stream";
    private static final String CONSUMER_GROUP = "apply-processors";
    private static final String CONSUMER_NAME = "apply-db-saver";

    private final ApplyStreamListener applyStreamListener;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofMillis(100))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);

        // 컨슈머 그룹으로 리스닝 설정
        container.receiveAutoAck(
                Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                StreamOffset.create(APPLY_STREAM, ReadOffset.lastConsumed()),
                applyStreamListener
        );

        container.start();
        log.info("Started Stream message listener container");

        return container;
    }
}
