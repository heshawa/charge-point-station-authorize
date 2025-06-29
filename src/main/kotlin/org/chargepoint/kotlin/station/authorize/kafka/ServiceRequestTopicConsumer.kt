package org.chargepoint.kotlin.station.authorize.kafka

import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.chargepoint.kotlin.station.authorize.service.StationAuthorizationService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener

@Configuration
class ServiceRequestTopicConsumer(
    private val stationAuthorizationService: StationAuthorizationService
) {
    var log = LoggerFactory.getLogger(ServiceRequestTopicConsumer::class.java)

    @KafkaListener(topics = ["charge-point-service-request"], groupId = "my-consumer-group")
    fun readTopicMessages(message : ServiceRequestContext){
        log.info("Message received from topic. Message Id: ${message.requestCorrelationId}")
        stationAuthorizationService.processMessagesFromKafka(message)
    }
 
    
}