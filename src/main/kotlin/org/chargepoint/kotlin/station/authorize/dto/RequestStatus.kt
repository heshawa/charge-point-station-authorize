package org.chargepoint.kotlin.station.authorize.dto

enum class RequestStatus(statusId : Int, value:String) {
    SUBMITTED(1,"submitted"),
    PUBLISHED(2,"published"),
    PROCESSED(3,"processed"),
    FAILED(4, "failed")
    
}