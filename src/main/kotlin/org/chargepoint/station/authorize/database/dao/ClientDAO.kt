package org.chargepoint.station.authorize.database.dao

import org.chargepoint.station.authorize.database.entity.EvClient
import java.util.UUID

interface ClientDAO {
    fun getClient(clientId : UUID) : EvClient
}