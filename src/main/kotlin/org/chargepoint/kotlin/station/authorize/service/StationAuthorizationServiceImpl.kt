package org.chargepoint.kotlin.station.authorize.service

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.chargepoint.kotlin.station.authorize.dto.CallbackRequestBody
import org.chargepoint.kotlin.station.authorize.dto.CallbackResponseBody
import org.chargepoint.kotlin.station.authorize.dto.ChargingApprovalStatus
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.chargepoint.kotlin.station.authorize.kafka.RetryStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import kotlin.time.Duration

@Service
class StationAuthorizationServiceImpl(
    private val stationACLService: StationACLService,
    private val retryStrategy: RetryStrategy,
    private val webClient: WebClient
):StationAuthorizationService {

    val log : Logger = LoggerFactory.getLogger(StationAuthorizationServiceImpl::class.java)
    
    override fun processMessagesFromKafka(message: ServiceRequestContext) {
        log.info("Starting to process message from the topic. Message Id: ${message.requestCorrelationId}")
    }

    override suspend fun chargingSessionConfirmed(url : String,callbackReqBody : CallbackRequestBody) : Boolean{
        val successResponse: Boolean = webClient
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(callbackReqBody)
            .awaitExchange{response ->
                if(response.statusCode().is2xxSuccessful){
                    var isConfirmed = response.awaitBody<CallbackResponseBody>().success
                    if(isConfirmed){
                        log.info("Charging appointment is confirmed. Driver Id : ${callbackReqBody.driverToken}, Station Id: ${callbackReqBody.stationId}")
                    } else {
                        log.warn("Charging appointment is denied. Driver Id : ${callbackReqBody.driverToken}, Station Id: ${callbackReqBody.stationId}")
                    }
                    return@awaitExchange isConfirmed
                }else{
                    log.error("Charging appointment could not confirm. Driver Id : ${callbackReqBody.driverToken}, Station Id: ${callbackReqBody.stationId}")
                    return@awaitExchange false
                }
            }
        
        return successResponse
    }

    override suspend fun sendPushNotification(request: CallbackRequestBody) {
        //TODO: At system start - Initialize firebase app(1 time)
        //TODO: send push notification
        log.info("Push notification sent to firebase. Driver id: ${request.driverToken}, Station id: ${request.stationId}")
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