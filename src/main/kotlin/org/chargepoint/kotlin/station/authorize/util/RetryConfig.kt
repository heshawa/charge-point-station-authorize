package org.chargepoint.kotlin.station.authorize.util

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration

@Configuration
//@ConfigurationProperties(prefix = "message.retry")
class RetryConfig {
    val maxAttempts = 3
    val initialDelay: Duration = Duration.parse("0.1s")
    val maxDelay: Duration = Duration.parse("30s")
    val multiplier = 2.0
    val dlqDelay: Duration = Duration.parse("5m")
    val dlqMaxAttempts = 5

}