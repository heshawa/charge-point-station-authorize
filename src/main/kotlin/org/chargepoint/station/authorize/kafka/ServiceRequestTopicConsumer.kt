package org.chargepoint.station.authorize.kafka

import kotlinx.coroutines.*
import org.chargepoint.station.authorize.dto.CallbackRequestBody
import org.chargepoint.station.authorize.dto.ChargingApprovalStatus
import org.chargepoint.station.authorize.dto.RequestStatus
import org.chargepoint.station.authorize.dto.ServiceRequestContext
import org.chargepoint.station.authorize.service.StationAuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.time.Duration

@Component
class ServiceRequestTopicConsumer(
    private val stationAuthorizationService: StationAuthorizationService,
    private val retryStrategy: RetryStrategy,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    @Value("\${kafka.topics.charge-point-server-request}")
    val consumerTopicName : String,
    @Value("\${spring.kafka.consumer.group.id}")
    val consumerGroupId : String
) {
    val safetyAttemptCount = 10
    val log: Logger = LoggerFactory.getLogger(ServiceRequestTopicConsumer::class.java)

    @KafkaListener(topics = ["#{__listener.consumerTopicName}"], groupId = "#{__listener.consumerGroupId}")
    suspend fun readTopicMessages(message : ServiceRequestContext, ack: Acknowledgment){
        log.info("Message received from topic. Message Id: ${message.requestCorrelationId}")
        ack.acknowledge()

        try {
            //TODO: Update received message in DB
            coroutineScope.launch {
                var lastException: Exception? = message.lastError

                while (message.lastRetryAttempt < safetyAttemptCount) { // Safety limit
                    message.lastRetryAttempt = message.lastRetryAttempt.inc() //Update retry attempt number

                    try {
                        // Process the message
                        val isAllowedToCharge = stationAuthorizationService.isEligibleToChargeAtStation(message)
                        val requestStatus: ChargingApprovalStatus = isAllowedToCharge.takeIf { it }?.let { ChargingApprovalStatus.ALLOWED }
                                ?: message.status.takeIf { it == RequestStatus.FAILED } ?.let { ChargingApprovalStatus.NOT_ALLOWED }
                                ?: message.status.takeIf { it == RequestStatus.SUBMITTED } ?.let { ChargingApprovalStatus.UNKNOWN }
                                ?: ChargingApprovalStatus.INVALID

                        //Send request to callback url when processing complete
                        val requestBody = CallbackRequestBody(
                            message.clientUUID.toString(),
                            message.stationUUID.toString(),
                            requestStatus.description
                        )
                        var chargingSlotConfirmed = stationAuthorizationService
                            .chargingSessionConfirmed(message.callbackUrl,requestBody)
                        log.info("Message successfully processed. Correlation Id: ${message.requestCorrelationId}")
                        chargingSlotConfirmed.takeIf { it }?.let { stationAuthorizationService.sendPushNotification(requestBody) }
                            ?: {
                                log.warn("Charging request for the station denied. Driver id: ${requestBody.driverToken}, Station id: ${requestBody.stationId}")
                            }
                        
                        //TODO: Update DB that processed successfully
                        return@launch
                    } catch (exception: Exception) { //Catch errors
                        lastException = exception;

                        // Check if we should retry
                        if (!retryStrategy.shouldRetry(message.lastRetryAttempt, exception)) {
                            throw exception;
                        }

                        // Calculate delay and wait
                        var delay = retryStrategy.getDelay(message.lastRetryAttempt);
                        log.warn(
                            "Processing failed, retrying in ${delay.toString()}. " +
                                    "Attempt: ${message.lastRetryAttempt}, " +
                                    "Message: ${message.requestCorrelationId}", exception
                        );

                        try {
                            delay(delay ?: Duration.parse("5s"))
                        } catch (interruptedEx: InterruptedException) {
                            Thread.currentThread().interrupt();
                            throw RuntimeException("Interrupted during retry delay", interruptedEx);
                        }
                    }
                }

                message.lastRetryAttempt.takeIf { it >= safetyAttemptCount }?.let {
                    throw RuntimeException("Max retry attempts exceeded", lastException)
                }
            }
        } catch (exception: Exception) {
            //TODO: publish to dead letter queue when max retry attempts reached
        }
    }
 
    
}