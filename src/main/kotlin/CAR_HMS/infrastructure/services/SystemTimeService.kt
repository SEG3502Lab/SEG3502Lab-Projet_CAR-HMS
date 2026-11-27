package CAR_HMS.infrastructure.services

import CAR_HMS.application.services.TimeService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
@Profile("!contracts")
class SystemTimeService : TimeService {

    override fun now(): LocalDateTime =
        LocalDateTime.now(ZoneId.of("America/Toronto"))
}