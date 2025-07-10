package org.chargepoint.station.authorize.service

import java.util.UUID

interface StationACLService {
    fun isAllowClientBacked(clientId:UUID, stationId:UUID) : Boolean
    fun isAllowClient(clientId:UUID, stationId:UUID) : Boolean
}