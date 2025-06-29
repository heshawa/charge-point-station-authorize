package org.chargepoint.kotlin.station.authorize.exception

import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.validation.ValidationException
import org.apache.kafka.common.errors.TimeoutException
import org.chargepoint.kotlin.station.authorize.kafka.ErrorType
import org.springframework.stereotype.Component
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.sql.SQLException

@Component
class StationConsumerExceptionClassifier {
    fun classifyException(exception : Throwable) : ErrorType{
        if(exception is ConnectException ||
            exception is SocketTimeoutException ||
            exception is TimeoutException){
            return ErrorType.TRANSIENT
        }
        
        if(exception is SQLException){
            if (exception.errorCode == 1205 || //Deadlock
                exception.errorCode == 1213 || //Lock wait timeout
                exception.sqlState.startsWith("08")) { //Connection errors
                return ErrorType.TRANSIENT;
            }
            return ErrorType.PERMANENT;
        }

        // Validation errors
        if (exception is IllegalArgumentException ||
            exception is JsonProcessingException ||
            exception is ValidationException
        ) {
            return ErrorType.PERMANENT;
        }

        return ErrorType.UNKNOWN;

    }
}