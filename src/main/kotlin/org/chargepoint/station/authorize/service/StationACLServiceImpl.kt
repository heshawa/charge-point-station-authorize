package org.chargepoint.station.authorize.service

import org.chargepoint.station.authorize.database.entity.ChargingStation
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
        //TODO: Not yet implemented
        val evClient : EvClient = clientRepository.findById(clientId).orElse(null)
        evClient.takeIf { it == null }?.let { return false }

        val chargingStation : ChargingStation = chargingStationRepository.findById(stationId).orElse(null)
        chargingStation.takeIf { it == null }?.let { return false }
        
        val clientCategory : Char = evClient.clientSubscriptionCategory!!
        val stationType : Int = chargingStation.stationType!!

        val clientAccessibility = clientStationAccessibilityRepository.findByClientCategoryAndStationType(clientCategory, stationType)
        
        return clientAccessibility.takeIf { it == null }?.let { return false } ?: return true
    }
}