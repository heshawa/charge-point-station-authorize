package org.chargepoint.kotlin.station.authorize.service

import kotlinx.coroutines.*
import org.chargepoint.kotlin.station.authorize.dto.CallbackRequestBody
import org.chargepoint.kotlin.station.authorize.dto.ChargingApprovalStatus
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.chargepoint.kotlin.station.authorize.kafka.RetryStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class StationAuthorizationServiceImpl(
    private val stationACLService: StationACLService,
    private val retryStrategy: RetryStrategy,
):StationAuthorizationService {

    val log : Logger = LoggerFactory.getLogger(StationAuthorizationServiceImpl::class.java)
    
    override fun processMessagesFromKafka(message: ServiceRequestContext) {
        log.info("Starting to process message from the topic. Message Id: ${message.requestCorrelationId}")
    }

    override fun invokeCallBackUrl(url : String,callbackReqBody : CallbackRequestBody){
        
    }

    override fun isEligibleToChargeAtStation(message : ServiceRequestContext) : Boolean{
        return try {
            //check client eligibility to use station
            val isAllowed = stationACLService.isAllowClient(message.clientUUID, message.stationUUID)

            if (!isAllowed) {
                log.warn("Client ${message.clientUUID} is not allowed to use station ${message.stationUUID}")
                return false
            }

            log.info("Message processed successfully for client: ${message.clientUUID}")
            true

        } catch (exception: Exception) {
            log.error("Error processing message for client ${message.clientUUID}: ${exception.message}", exception)
            throw exception // Re-throw to be handled by retry logic
        }
    }
}