package org.chargepoint.station.authorize.kafka

import org.chargepoint.station.authorize.exception.StationConsumerExceptionClassifier
import org.chargepoint.station.authorize.util.RetryConfig
import org.springframework.stereotype.Component
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration

@Component
class ExponentialBackoffRetryStrategyImpl(private val retryConfig: RetryConfig,
    private val stationConsumerExceptionClassifier: StationConsumerExceptionClassifier
) : RetryStrategy{
    override fun shouldRetry(attemptCount: Int, exception: Throwable?): Boolean {
        if(attemptCount >= retryConfig.maxAttempts){}
        attemptCount.takeIf { it >= retryConfig.maxAttempts}?.let { return false }

        //Categorize errors(Worth re-trying or constant errors)
        val errorType = exception?.let { stationConsumerExceptionClassifier.classifyException(it) }

        // Not retrying permanent errors
        errorType.takeIf { it == ErrorType.PERMANENT }?.let { return false }

        // Always retry transient errors (up to max attempts)
        errorType.takeIf { it == ErrorType.TRANSIENT }?.let { return true }

        // Retry unknown errors cautiously
        return attemptCount < 2;
    }

    override fun getDelay(attemptCount: Int): Duration? {
        var delayDuration = retryConfig.initialDelay * retryConfig.multiplier.pow(attemptCount-1)

        // Add random interval to prevent process many together
        val randomInterval = (delayDuration * 0.1 * Random.nextDouble())
        
        delayDuration+=randomInterval
        
        return Duration.parse("${delayDuration.div(1000)}s")
    }
}