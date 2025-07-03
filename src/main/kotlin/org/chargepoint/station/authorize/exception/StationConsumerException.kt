package org.chargepoint.station.authorize.exception

import java.lang.Exception

class StationConsumerException : RuntimeException {
    constructor(message : String) : super (message)
    
    constructor(message: String, exception: Exception) : super(message, exception)
}