package org.chargepoint.kotlin.station.authorize

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StationAuthorize

fun main(args: Array<String>) {
	runApplication<StationAuthorize>(*args)
}
