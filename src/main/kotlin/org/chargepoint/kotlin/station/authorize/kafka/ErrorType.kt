package org.chargepoint.kotlin.station.authorize.kafka

enum class ErrorType(val value : Int, val description : String) {
    TRANSIENT(1,"transient"),//Temporary issues (network, timeout)
    PERMANENT(2,"permanent"),//Data corruption, validation errors
    UNKNOWN(3,"unknown")// Unknown errors

}