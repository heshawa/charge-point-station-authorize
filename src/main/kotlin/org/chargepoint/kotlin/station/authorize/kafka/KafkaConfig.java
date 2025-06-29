package org.chargepoint.kotlin.station.authorize.kafka;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaConfig {
	
	Logger log = LoggerFactory.getLogger(KafkaConfig.class);

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapConfig;
	@Value("${spring.kafka.consumer.key.deserializer}")
	private String keyDeSerializerClass;
	@Value("${spring.kafka.consumer.value.deserializer}")
	private String valueDeSerializerClass;

	@Value("${spring.kafka.properties.sasl.jaas.config}")
	private String  jassConfig;

	@Value("${spring.kafka.consumer.group.id}")
	private String  consumerGroupId;

	@Value("${spring.kafka.consumer.max.poll.records}")
	private String  consumerMaxPollRecords;

	@Value("${spring.kafka.consumer.session.timeout.ms}")
	private String  consumerSessionTimeOutMs;

	@Value("${spring.kafka.topics.charge-point-server-request}")
	private String  consumerTopicName;
	
	@Value("${spring.kafka.consumer.properties.spring.json.value.default.type}")
	private String  jsonDefaultType;

	@Value("${spring.kafka.security.protocol}")
	private String  securityProtocol;

	@Value("${spring.kafka.sasl.mechanism}")
	private String  securitySaslMechanism;

	@Value("${spring.kafka.producer.acks}")
	private String  producerAckConfig;

	@Value("${spring.kafka.producer.retries}")
	private String  producerRetries;

	@Value("${spring.kafka.producer.batch.size}")
	private String  producerBatchSize;

	@Value("${spring.kafka.producer.linger.ms}")
	private String  producerLingerMs;

	@Value("${spring.kafka.producer.buffer.memory}")
	private String  producerBufferMemory;

	@Value("${spring.kafka.producer.compression.type}")
	private String  producerCompressionType;

	@Value("${spring.kafka.producer.max.request.size}")
	private String  producerMaxRequestSize;


	@Bean
	public ConsumerFactory<String, ServiceRequestContext> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapConfig);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
//		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializerClass);
//		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializerClass);
		config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,consumerMaxPollRecords);

		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);

		config.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
		config.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);

		config.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
		config.put(JsonDeserializer.VALUE_DEFAULT_TYPE,jsonDefaultType);
		config.put("spring.json.use.type.headers", false);

		config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		config.put(SaslConfigs.SASL_JAAS_CONFIG,jassConfig);

		//config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		return new DefaultKafkaConsumerFactory(config);
	}

	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
		// Send failed records to .DLT topic
		log.warn("Executing deserialize error handler");
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
				(r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition()));

		// Retry 3 times with 1s interval
		FixedBackOff fixedBackOff = new FixedBackOff(1000L, 3L);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

		errorHandler.setRetryListeners((record, ex, attempt) -> {
			log.warn("Failed record on attempt {}: {}", attempt, record.value(), ex);
		});

		return errorHandler;
	}

	@Bean
	@Primary
	public ConcurrentKafkaListenerContainerFactory<String, ServiceRequestContext> kafkaListenerContainerFactory(
			ConsumerFactory<String, ServiceRequestContext> consumerFactory,
			DefaultErrorHandler errorHandler) {

		ConcurrentKafkaListenerContainerFactory<String, ServiceRequestContext> factory =
				new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(errorHandler);

		return factory;
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapConfig);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		config.put(SaslConfigs.SASL_JAAS_CONFIG,jassConfig);

		config.put(ProducerConfig.ACKS_CONFIG,producerAckConfig);
		config.put(ProducerConfig.RETRIES_CONFIG,producerRetries);
		config.put(ProducerConfig.BATCH_SIZE_CONFIG,producerBatchSize);
		config.put(ProducerConfig.LINGER_MS_CONFIG,producerLingerMs);
		config.put(ProducerConfig.BUFFER_MEMORY_CONFIG,producerBufferMemory);
		config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,producerCompressionType);
		config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,producerMaxRequestSize);

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}



	@PostConstruct
	public void validateBootstrap() {
		log.info("Kafka template created. Kafka bootstrap: " + bootstrapConfig);
	}
}
