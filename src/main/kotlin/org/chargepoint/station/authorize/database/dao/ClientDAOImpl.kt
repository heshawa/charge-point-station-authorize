package org.chargepoint.station.authorize.database.dao

import org.chargepoint.station.authorize.database.entity.EvClient
import org.chargepoint.station.authorize.database.repository.ClientRepository
import org.chargepoint.station.authorize.exception.StationConsumerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ClientDAOImpl(
    private val clientRepository: ClientRepository
) : ClientDAO {
    
    val log : Logger = LoggerFactory.getLogger(ClientDAOImpl::class.java)
    override fun getClient(clientId: UUID): EvClient {
        return clientRepository.findById(clientId)
            .orElseThrow { 
                log.error("Client id is not existing. Client Id: $clientId ")
                StationConsumerException("Invalid client Id: $clientId") 
            }
            .also { 
                log.info("Successfully fetched client info. Client Id: $clientId") 
            }
    }
}