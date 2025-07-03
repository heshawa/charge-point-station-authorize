package org.chargepoint.kotlin.station.authorize.database.repository

import org.chargepoint.kotlin.station.authorize.database.entity.EvClient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClientRepository : JpaRepository<EvClient,UUID>{
}