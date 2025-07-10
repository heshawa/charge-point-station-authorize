package org.chargepoint.station.authorize.database.repository

import org.chargepoint.station.authorize.database.entity.ClientStationAccessibility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ClientStationAccessibilityRepository : JpaRepository<ClientStationAccessibility, Int> {
    fun findByClientCategoryAndStationType(clientCategory:Char,stationType:Int) : Optional<ClientStationAccessibility>
    
    @Query("SELECT ca from ClientStationAccessibility ca LEFT JOIN EvClient c ON c.clientSubscriptionCategory=ca.clientCategory LEFT JOIN ChargingStation s ON s.stationType=ca.stationType WHERE s.stationId=:stationId AND c.clientId=:clientId")
    fun getClientStationAccessibility(clientId:UUID,stationId:UUID) : Optional<ClientStationAccessibility>
}