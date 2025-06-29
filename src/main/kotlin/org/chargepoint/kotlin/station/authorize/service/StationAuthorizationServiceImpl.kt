package org.chargepoint.kotlin.station.authorize.service

import kotlinx.coroutines.*
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext
import org.chargepoint.kotlin.station.authorize.kafka.RetryStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class StationAuthorizationServiceImpl(
    private val retryStrategy: RetryStrategy,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
):StationAuthorizationService {

    val log : Logger = LoggerFactory.getLogger(StationAuthorizationServiceImpl::class.java)
    val safetyAttemptCount = 10
    
    override fun processMessagesFromKafka(message: ServiceRequestContext) {
        log.info("Starting to process message from the topic. Message Id: ${message.requestCorrelationId}")
    }

    override suspend fun processMessagesFromKafkaAsync(message: ServiceRequestContext): Job {
        //TODO: Update received message in DB
        return coroutineScope.launch { 
            var lastException : Exception? = message.lastError

            while (message.lastRetryAttempt < safetyAttemptCount) { // Safety limit
                message.lastRetryAttempt = message.lastRetryAttempt.inc() //Update retry attempt number

                try {
                    // Process the message
                    processMessage(message);
                    log.info("Message successfully processed. Correlation Id: ${message.requestCorrelationId}")
                    //TODO: Update DB that processed successfully
                    //TODO: Send request to callback url when processing complete
                    return@launch
                } catch (exception : Exception) { //Catch errors
                    lastException = exception;

                    // Check if we should retry
                    if (!retryStrategy.shouldRetry(message.lastRetryAttempt, exception)) {
                        throw exception;
                    }

                    // Calculate delay and wait
                    var delay = retryStrategy.getDelay(message.lastRetryAttempt);
                    log.warn("Processing failed, retrying in ${delay.toString()}. " +
                            "Attempt: ${message.lastRetryAttempt}, " +
                            "Message: ${message.requestCorrelationId}", exception);

                    try {
                        delay(delay?: Duration.parse("5s"))
                    } catch (interruptedEx : InterruptedException) {
                        Thread.currentThread().interrupt();
                        throw RuntimeException("Interrupted during retry delay", interruptedEx);
                    }
                }
            }
            
            message.lastRetryAttempt.takeIf { it >= safetyAttemptCount}?.let { 
                throw RuntimeException("Max retry attempts exceeded", lastException) 
            }
        }
    }

    suspend fun processMessage(message : ServiceRequestContext){
        // Add circuit breaker protection
        val validationJob = coroutineScope.launch { 
            try {
                //TODO: check client eligibility to use station
            }catch (exception : Exception){
                throw RuntimeException(exception)
            }
        }

        try {
            validationJob.join()
        } catch (exception : Exception) {
            // Unwrap the exception
            val cause = exception.cause;
            if (cause is RuntimeException) {
                if (cause.cause is Exception) {
                    throw cause.cause as Exception
                }
                throw cause;
            }
            throw exception;
        }
    }
}