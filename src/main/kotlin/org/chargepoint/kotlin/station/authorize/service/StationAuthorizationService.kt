package org.chargepoint.kotlin.station.authorize.service

import kotlinx.coroutines.Job
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext

interface StationAuthorizationService {
    fun processMessagesFromKafka(message:ServiceRequestContext)

    suspend fun processMessagesFromKafkaAsync(message:ServiceRequestContext) : Job
}