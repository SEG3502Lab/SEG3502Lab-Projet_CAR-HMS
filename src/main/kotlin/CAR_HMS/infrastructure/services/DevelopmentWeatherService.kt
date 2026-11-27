package CAR_HMS.infrastructure.services

import CAR_HMS.application.services.WeatherService
import CAR_HMS.application.services.WeatherServiceUnavailableException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Profile("local-dev & !contracts")
class DevelopmentWeatherService : WeatherService {

    override fun forecast(
        location: String,
        date: LocalDate
    ): String {

        throw WeatherServiceUnavailableException()
    }
}