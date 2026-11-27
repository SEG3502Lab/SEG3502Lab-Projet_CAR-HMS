package CAR_HMS.infrastructure.web

import CAR_HMS.domain.common.DomainException
import org.springframework.context.annotation.Profile
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Profile("!contracts")
class ApiExceptionHandler {

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        exception: DomainException
    ): ResponseEntity<ApiError> {

        val status = when (exception.reason) {

            "connexion requise" ->
                HttpStatus.UNAUTHORIZED

            "accès refusé",
            "organisateur non certifié" ->
                HttpStatus.FORBIDDEN

            else ->
                HttpStatus.BAD_REQUEST
        }

        return ResponseEntity
            .status(status)
            .body(ApiError(exception.reason))
    }

    @ExceptionHandler(
        DataIntegrityViolationException::class
    )
    fun handleDataIntegrityViolation():
        ResponseEntity<ApiError> =

        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiError(
                    "Conflit avec les données existantes"
                )
            )
}

data class ApiError(
    val message: String
)