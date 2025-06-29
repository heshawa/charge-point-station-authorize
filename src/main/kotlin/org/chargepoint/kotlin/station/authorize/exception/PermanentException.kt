package org.chargepoint.kotlin.station.authorize.exception

class PermanentException {
    constructor(message : String)

    constructor(message : String, exception : Throwable)
}