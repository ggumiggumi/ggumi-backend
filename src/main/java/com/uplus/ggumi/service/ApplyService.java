package com.uplus.ggumi.service;

import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.ggumi.domain.apply.Apply;
import com.uplus.ggumi.dto.apply.ApplyRequestDto;
import com.uplus.ggumi.repository.ApplyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

		applyRepository.existsByPhoneNumber(requestDto.getPhoneNumber());

		applyRepository.save(Apply.builder()
			.name(requestDto.getName())
			.phoneNumber(requestDto.getPhoneNumber())
			.applyTime(requestDto.getApplyTime())
			.build());

		return "SUCCESS";
	}

	public String applyVer5(ApplyRequestDto requestDto) {

		redisTemplate.opsForStream().add(StreamRecords.newRecord()
			.in("apply_stream")
			.ofObject(requestDto));

		return "SUCCESS";
	}

}
