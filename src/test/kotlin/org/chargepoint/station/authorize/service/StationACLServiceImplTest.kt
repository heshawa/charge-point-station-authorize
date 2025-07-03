package org.chargepoint.station.authorize.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.chargepoint.station.authorize.database.entity.ChargingStation
import org.chargepoint.station.authorize.database.entity.ClientStationAccessibility
import org.chargepoint.station.authorize.database.entity.EvClient
import org.chargepoint.station.authorize.database.repository.ChargingStationRepository
import org.chargepoint.station.authorize.database.repository.ClientRepository
import org.chargepoint.station.authorize.database.repository.ClientStationAccessibilityRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.AfterTest

class StationACLServiceImplTest {

    private lateinit var clientRepository: ClientRepository
    private lateinit var chargingStationRepository: ChargingStationRepository
    private lateinit var clientStationAccessibilityRepository: ClientStationAccessibilityRepository
    private lateinit var service : StationACLService

    val driverId = UUID.randomUUID()
    val stationId = UUID.randomUUID()
    
    @BeforeEach
    fun setup(){
        clientRepository = mockk()
        chargingStationRepository = mockk()
        clientStationAccessibilityRepository = mockk()

        service = StationACLServiceImpl(
            clientRepository,
            chargingStationRepository,
            clientStationAccessibilityRepository
        )
    }
    
    @AfterTest
    fun afterTest(){
        unmockkAll()
    }

    @Test
    fun `isAllowClient should return true when client category eligible to the station`(){
        val evClient = EvClient()
        evClient.clientId = driverId
        evClient.clientSubscriptionCategory = 'A'

        val chargingStation = ChargingStation()
        chargingStation.stationId = stationId
        chargingStation.stationType = 0

        val clientStationAccessibility = ClientStationAccessibility(1,'A',0)

        every { clientRepository.findById(driverId) } returns Optional.of(evClient)
        every { chargingStationRepository.findById(stationId) } returns Optional.of(chargingStation)
        every {
            clientStationAccessibilityRepository.findByClientCategoryAndStationType(
                evClient.clientSubscriptionCategory!!,
                chargingStation.stationType!!
            )
        } returns Optional.of(clientStationAccessibility)

        val allowClient = service.isAllowClient(driverId, stationId)

        assert(allowClient)
        verify { clientRepository.findById(driverId) }
        verify { chargingStationRepository.findById(stationId) }
        verify { clientStationAccessibilityRepository.findByClientCategoryAndStationType(
            evClient.clientSubscriptionCategory!!,
            chargingStation.stationType!!
        ) }

    }

    @Test
    fun `isAllowClient should return false when client is not existing`(){

        every { clientRepository.findById(driverId) } returns Optional.empty()

        val allowClient = service.isAllowClient(driverId, stationId)

        assert(!allowClient)
        verify { clientRepository.findById(driverId) }

    }

    @Test
    fun `isAllowClient should return false when station is not existing`(){
        val evClient = EvClient()
        evClient.clientId = driverId
        evClient.clientSubscriptionCategory = 'A'

        every { clientRepository.findById(driverId) } returns Optional.of(evClient)
        every { chargingStationRepository.findById(stationId) } returns Optional.empty()

        val allowClient = service.isAllowClient(driverId, stationId)

        assert(!allowClient)
        verify { clientRepository.findById(driverId) }
        verify { chargingStationRepository.findById(stationId) }

    }

    @Test
    fun `isAllowClient should return false when client ACL is not existing`(){
        val evClient = EvClient()
        evClient.clientId = driverId
        evClient.clientSubscriptionCategory = 'A'

        val chargingStation = ChargingStation()
        chargingStation.stationId = stationId
        chargingStation.stationType = 0

        every { clientRepository.findById(driverId) } returns Optional.of(evClient)
        every { chargingStationRepository.findById(stationId) } returns Optional.of(chargingStation)
        every {
            clientStationAccessibilityRepository.findByClientCategoryAndStationType(
                evClient.clientSubscriptionCategory!!,
                chargingStation.stationType!!
            )
        } returns Optional.empty()

        val allowClient = service.isAllowClient(driverId, stationId)

        assert(!allowClient)
        verify { clientRepository.findById(driverId) }
        verify { chargingStationRepository.findById(stationId) }
        verify { clientStationAccessibilityRepository.findByClientCategoryAndStationType(
            evClient.clientSubscriptionCategory!!,
            chargingStation.stationType!!
        ) }

    }

}