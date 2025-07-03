package org.chargepoint.kotlin.station.authorize.database.repository

import org.chargepoint.kotlin.station.authorize.database.entity.ClientStationAccessibility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientStationAccessibilityRepository : JpaRepository<ClientStationAccessibility, Int> {
    fun findByClientCategoryAndStationType(clientCategory:Char,stationType:Int) : ClientStationAccessibility
}