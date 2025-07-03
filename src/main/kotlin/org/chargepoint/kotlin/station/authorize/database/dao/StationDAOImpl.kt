package org.chargepoint.kotlin.station.authorize.database.dao

import org.chargepoint.kotlin.station.authorize.database.entity.ChargingStation
import org.chargepoint.kotlin.station.authorize.database.repository.ChargingStationRepository
import org.chargepoint.kotlin.station.authorize.exception.StationConsumerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class StationDAOImpl(
    private val stationRepository: ChargingStationRepository
) : StationDAO {
    
    val log : Logger = LoggerFactory.getLogger(StationDAOImpl::class.java)
    override fun getStation(stationId : UUID) : ChargingStation {
        return stationRepository.findById(stationId)
            .orElseThrow { 
                log.error("Station is not existing. Station Id: $stationId")
                StationConsumerException("Invalid station Id: $stationId") 
            }
            .also { 
                log.info("Station details fetched successfully. Station Id: $stationId")
            }
    }
}