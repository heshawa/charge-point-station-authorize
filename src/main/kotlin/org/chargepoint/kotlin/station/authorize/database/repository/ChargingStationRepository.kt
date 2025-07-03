package org.chargepoint.kotlin.station.authorize.database.repository

import org.chargepoint.kotlin.station.authorize.database.entity.ChargingStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChargingStationRepository : JpaRepository<ChargingStation,UUID> {
}