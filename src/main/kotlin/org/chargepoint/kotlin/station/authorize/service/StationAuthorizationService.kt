package org.chargepoint.kotlin.station.authorize.service

import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext

interface StationAuthorizationService {
    fun processMessagesFromKafka(message:ServiceRequestContext)
}