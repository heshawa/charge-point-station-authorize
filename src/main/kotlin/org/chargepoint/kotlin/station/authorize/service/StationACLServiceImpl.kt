package org.chargepoint.kotlin.station.authorize.service

import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

@Service
class StationACLServiceImpl : StationACLService {
    override fun isAllowClient(clientId: UUID, stationId: UUID): Boolean {
        //TODO: Not yet implemented
        return Random.nextBoolean()
    }
}