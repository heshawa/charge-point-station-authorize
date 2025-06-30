package org.chargepoint.kotlin.station.authorize.service

import java.util.UUID

interface StationACLService {
    fun isAllowClient(clientId:UUID, stationId:UUID) : Boolean
}