package org.chargepoint.station.authorize.database.repository

import org.chargepoint.station.authorize.database.entity.ClientStationAccessibility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ClientStationAccessibilityRepository : JpaRepository<ClientStationAccessibility, Int> {
    fun findByClientCategoryAndStationType(clientCategory:Char,stationType:Int) : Optional<ClientStationAccessibility>
}