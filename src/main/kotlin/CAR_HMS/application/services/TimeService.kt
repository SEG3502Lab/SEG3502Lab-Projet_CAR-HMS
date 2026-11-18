package CAR_HMS.application.services

import java.time.LocalDateTime

interface TimeService {
    fun now(): LocalDateTime
}
