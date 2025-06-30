package org.chargepoint.kotlin.station.authorize.service

import kotlinx.coroutines.Job
import org.chargepoint.kotlin.station.authorize.dto.CallbackRequestBody
import org.chargepoint.kotlin.station.authorize.dto.ServiceRequestContext

interface StationAuthorizationService {
    fun processMessagesFromKafka(message:ServiceRequestContext)

    fun isEligibleToChargeAtStation(message : ServiceRequestContext) : Boolean

    suspend fun invokeCallBackUrl(url : String,callbackReqBody : CallbackRequestBody)
}