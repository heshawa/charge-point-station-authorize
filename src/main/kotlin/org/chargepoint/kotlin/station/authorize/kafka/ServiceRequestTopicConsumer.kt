package org.chargepoint.kotlin.station.authorize.kafka

import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.chargepoint.kotlin.station.authorize.service.StationAuthorizationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener

@Configuration
class ServiceRequestTopicConsumer(
    private val stationAuthorizationService: StationAuthorizationService,
    @Value("\${kafka.topics.charge-point-server-request}")
    val consumerTopicName : String,
    @Value("\${spring.kafka.consumer.group.id}")
    val consumerGroupId : String
) {
    var log = LoggerFactory.getLogger(ServiceRequestTopicConsumer::class.java)

    @KafkaListener(topics = ["#{__listener.consumerTopicName}"], groupId = "#{__listener.consumerGroupId}")
    fun readTopicMessages(message : ServiceRequestContext){
        log.info("Message received from topic. Message Id: ${message.requestCorrelationId}")
        stationAuthorizationService.processMessagesFromKafka(message)
    }
 
    
}