package org.chargepoint.kotlin.station.authorize.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

class ServiceRequestContext( val clientUUID:UUID,
                             val stationUUID:UUID,
                             val callbackUrl : String
){
    val timeStamp:String = Clock.System.now().toString()
    var status:RequestStatus = RequestStatus.SUBMITTED
    var lastRetryAttempt:Int = 0
    lateinit var requestCorrelationId:UUID
    var lastError: Exception? = null
    var lastModifiedTime:String? = null
}
