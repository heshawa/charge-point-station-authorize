package org.chargepoint.kotlin.station.authorize.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext;
import org.chargepoint.kotlin.station.authorize.service.StationAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class ServiceRequestTopicConsumerJava {
	
	Logger log = LoggerFactory.getLogger(ServiceRequestTopicConsumerJava.class);

	@Autowired
	private KafkaConsumer consumer;
	
	@Autowired
	private StationAuthorizationService stationAuthorizationService;

	@KafkaListener(topics = "charge-point-service-request", groupId = "my-consumer-group")
	public void listen(ServiceRequestContext message) throws Exception {
		System.out.println("Received message: " + message.getRequestCorrelationId());
		log.info("Heshawa found: " + message.getRequestCorrelationId());
		stationAuthorizationService.processMessagesFromKafka(message);
	}



}
