package org.chargepoint.kotlin.station.authorize.dto

enum class ChargingApprovalStatus(
    val value:Int,
    val description:String
) {
    NOT_ALLOWED(0,"not_allowed"),
    ALLOWED(1,"allowed"),
    INVALID(2,"invalid"),
    UNKNOWN(3,"unknown")
}