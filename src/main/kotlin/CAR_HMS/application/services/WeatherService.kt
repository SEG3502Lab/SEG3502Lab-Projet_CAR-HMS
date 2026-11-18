package CAR_HMS.application.services

import java.time.LocalDate

class WeatherServiceUnavailableException : RuntimeException("service météo indisponible")

interface WeatherService {
    /** Retourne la condition prévue ; lève [WeatherServiceUnavailableException] si le service est hors d'usage. */
    fun forecast(location: String, date: LocalDate): String
}
