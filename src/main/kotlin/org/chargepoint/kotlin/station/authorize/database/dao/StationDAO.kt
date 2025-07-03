package org.chargepoint.kotlin.station.authorize.database.dao

import org.chargepoint.kotlin.station.authorize.database.entity.ChargingStation
import java.util.*

interface StationDAO {
    fun getStation(stationId : UUID) : ChargingStation
}