// package com.uplus.ggumi.config.kafka;
//
// import org.apache.kafka.clients.consumer.ConsumerRecord;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.support.Acknowledgment;
// import org.springframework.stereotype.Component;
//
// import com.uplus.ggumi.domain.apply.Apply;
// import com.uplus.ggumi.dto.apply.ApplyRequestDto;
// import com.uplus.ggumi.repository.ApplyRepository;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RequiredArgsConstructor
// @Component
// public class ApplyConsumer {
//
// 	private final ApplyRepository applyRepository;
//
// 	@KafkaListener(topics = "apply", groupId = "ggumi")
// 	public void listen(ConsumerRecord<String, ApplyRequestDto> record) {
// 		ApplyRequestDto applyRequestDto = record.value();
// 		if (applyRequestDto != null) {
// 			Apply apply = Apply.builder()
// 				.name(applyRequestDto.getName())
// 				.phoneNumber(applyRequestDto.getPhoneNumber())
// 				.applyTime(applyRequestDto.getApplyTime())
// 				.build();
// 			applyRepository.save(apply);
// 		}
// 	}
//
// }
