package org.chargepoint.kotlin.station.authorize.database.dao

import org.chargepoint.kotlin.station.authorize.database.entity.EvClient
import java.util.UUID

interface ClientDAO {
    fun getClient(clientId : UUID) : EvClient
}