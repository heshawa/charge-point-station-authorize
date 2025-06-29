package org.chargepoint.kotlin.station.authorize.service

import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StationAuthorizationServiceImpl:StationAuthorizationService {

    val log : Logger = LoggerFactory.getLogger(StationAuthorizationServiceImpl::class.java)
    
    override fun processMessagesFromKafka(message: ServiceRequestContext) {
        log.info("Starting to process message from the topic. Message Id: ${message.requestCorrelationId}")
    }
}