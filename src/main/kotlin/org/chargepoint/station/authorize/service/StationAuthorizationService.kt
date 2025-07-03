package org.chargepoint.station.authorize.service

import org.chargepoint.station.authorize.dto.CallbackRequestBody
import org.chargepoint.station.authorize.dto.ServiceRequestContext

interface StationAuthorizationService {
    fun isEligibleToChargeAtStation(message : ServiceRequestContext) : Boolean

    suspend fun chargingSessionConfirmed(url : String,callbackReqBody : CallbackRequestBody) : Boolean
    
    suspend fun sendPushNotification(request:CallbackRequestBody)
}