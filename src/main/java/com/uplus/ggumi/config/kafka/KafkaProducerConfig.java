//package com.uplus.ggumi.config.kafka;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import com.uplus.ggumi.dto.apply.ApplyRequestDto;
//
//@Configuration
//public class KafkaProducerConfig {
//
//	@Bean
//	public ProducerFactory<String, ApplyRequestDto> producerFactory() {
//		Map<String, Object> config = new HashMap<>();
//		//Producer가 처음 연결할 kafka 브로커의 위치 설정
//		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//		//Producer가 Key와 Value값의 데이터를 Kafka 브로커로 전송하기 전에 데이터를 byte array로 변환하는 데 사용하는 직렬화 메커니즘을 설정
//		//Kafka는 네트워크를 통해 데이터를 전송하기에, 객체를 byte array로 변환하는 직렬화 과정이 필요
//		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//		return new DefaultKafkaProducerFactory<>(config);
//	}
//
//	@Bean
//	public KafkaTemplate<String, ApplyRequestDto> kafkaTemplate() {
//		return new KafkaTemplate<>(producerFactory());
//	}
//
//}
