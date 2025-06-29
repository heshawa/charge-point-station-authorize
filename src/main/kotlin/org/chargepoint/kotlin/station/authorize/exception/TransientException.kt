package org.chargepoint.kotlin.station.authorize.exception

class TransientException {
    constructor(message : String)

    constructor(message : String, exception : Throwable)
}