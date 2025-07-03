package org.chargepoint.station.authorize.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "client_station_accessibility",
    uniqueConstraints = [UniqueConstraint(columnNames = ["client_category","station_type"])])
data class ClientStationAccessibility(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "client_category", nullable = false)
    val clientCategory : Char,

    @Column(name = "station_type", nullable = false) 
    val stationType : Int

) {
}