//package com.uplus.ggumi.config.kafka;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import com.uplus.ggumi.dto.apply.ApplyRequestDto;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//	@Bean
//	public ConsumerFactory<String, ApplyRequestDto> consumerFactory() {
//		Map<String, Object> config = new HashMap<>();
//		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//		config.put(ConsumerConfig.GROUP_ID_CONFIG, "ggumi"); //group_id를 지정하여 같은 구릅은 같은 토픽을 소비
//		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//
//		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//			ErrorHandlingDeserializer.class.getName());
//		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
//			JsonDeserializer.class.getName());
//		config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//		return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(ApplyRequestDto.class));
//	}
//
//	@Bean
//	public ConcurrentKafkaListenerContainerFactory<String, ApplyRequestDto> kafkaListenerContainerFactory() {
//		ConcurrentKafkaListenerContainerFactory<String, ApplyRequestDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		factory.setConsumerFactory(consumerFactory());
//
//		return factory;
//	}
//
//}
