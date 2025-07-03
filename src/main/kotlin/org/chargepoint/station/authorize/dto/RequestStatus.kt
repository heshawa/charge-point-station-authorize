package org.chargepoint.station.authorize.dto

enum class RequestStatus(val statusId : Int, val value:String) {
    SUBMITTED(1,"submitted"),
    PUBLISHED(2,"published"),
    PROCESSED(3,"processed"),
    FAILED(4, "failed"),
    UNKNOWN(5, "unknown")
    
}