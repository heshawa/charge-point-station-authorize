package org.chargepoint.station.authorize.service

import org.chargepoint.station.authorize.database.entity.ChargingStation
import org.chargepoint.station.authorize.database.entity.ClientStationAccessibility
import org.chargepoint.station.authorize.database.entity.EvClient
import org.chargepoint.station.authorize.database.repository.ChargingStationRepository
import org.chargepoint.station.authorize.database.repository.ClientRepository
import org.chargepoint.station.authorize.database.repository.ClientStationAccessibilityRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class StationACLServiceImpl(
    private val clientRepository: ClientRepository,
    private val chargingStationRepository: ChargingStationRepository,
    private val clientStationAccessibilityRepository: ClientStationAccessibilityRepository
) : StationACLService {
    override fun isAllowClient(clientId: UUID, stationId: UUID): Boolean {
        val evClient : EvClient = clientRepository.findById(clientId).orElse(null) ?: return false

        val chargingStation : ChargingStation = chargingStationRepository.findById(stationId).orElse(null) ?: return false
        
        val clientCategory : Char = evClient?.clientSubscriptionCategory!!
        val stationType : Int = chargingStation.stationType!!

        clientStationAccessibilityRepository.findByClientCategoryAndStationType(
                clientCategory, stationType
            ).orElse(null) ?: return false
        
        return true
    }
}