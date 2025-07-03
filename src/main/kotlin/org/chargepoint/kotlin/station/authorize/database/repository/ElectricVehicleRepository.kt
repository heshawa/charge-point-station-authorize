package org.chargepoint.kotlin.station.authorize.database.repository

import org.chargepoint.kotlin.station.authorize.database.entity.ElectricVehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ElectricVehicleRepository : JpaRepository<ElectricVehicle,String> {
}