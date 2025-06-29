package org.chargepoint.kotlin.station.authorize.kafka

import kotlin.time.Duration

interface RetryStrategy {
    fun shouldRetry(attemptCount: Int, exception: Throwable?): Boolean
    fun getDelay(attemptCount: Int): Duration?

}