package com.uplus.ggumi.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	/* Redis 토픽 이름 정의 */
	public static final String APPLY_CHANNER = "apply_channel";

	/* Redis 와의 연결을 위한 Connection 생성 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	/*
	 * Redis 데이터 처리를 위한 템플릿 구성
	 * 해당 구성된 RedisTemplate 를 통해서 데이터 통신으로 처리되는 직렬화 수행
	 * */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

		/* Redis 연결 */
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		/* Key-Value 형태로 직렬화 수행 */
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		/* Hash Key-Value 형태로 직렬화 수행 */
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		/* 기본적으로 직렬화 수행 */
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());

		return redisTemplate;
	}

	/* 메시지 수신을 위한 Pub/Sub 메시지 리스너 컨테이너 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
			RedisConnectionFactory connectionFactory,
			ApplyMessageListener applyMessageListener
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(applyMessageListener, applyTopic());
		return container;
	}

	/* 응모 요청을 위한 토픽 정의 */
	@Bean
	public ChannelTopic applyTopic() {
		return new ChannelTopic(APPLY_CHANNER);
	}

	/* 응모 메시지 직렬화를 위한 템플릿 */
	@Bean
	public RedisTemplate<String, ApplyRequestDto> applyRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
		RedisTemplate<String, ApplyRequestDto> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);

		/* ApplyRequestDto를 위한 JSON 직렬화 설정 */
		Jackson2JsonRedisSerializer<ApplyRequestDto> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ApplyRequestDto.class);

		redisTemplate.setValueSerializer(serializer);
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		return redisTemplate;
	}
}
