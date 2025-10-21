package br.com.techhub.api.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

/**
 * Global exception handler for the entire application.
 * Intercepts specific exceptions and formats them into a consistent JSON error response.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles validation errors for request bodies (@RequestBody).
     * Triggered when an object annotated with @Valid fails validation.
     * @param ex The exception containing field-specific error details.
     * @return A ResponseEntity with a 400 Bad Request status and a detailed error body.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { error ->
            error.field to error.defaultMessage
        }

        val body: Map<String, Any> = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Validation Error",
            "message" to "One or more fields are invalid.",
            "errors" to errors
        )
        return ResponseEntity.badRequest().body(body)
    }

    /**
     * Handles validation errors for request parameters (@RequestParam, @PathVariable).
     * Triggered when a method parameter in a @Validated controller fails validation.
     * @param ex The exception containing constraint violation details.
     * @return A ResponseEntity with a 400 Bad Request status and a detailed error body.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errors = ex.constraintViolations.associate { violation ->
            violation.propertyPath.toString().substringAfterLast('.') to violation.message
        }

        val body: Map<String, Any> = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Validation Error",
            "message" to "One or more parameters are invalid.",
            "errors" to errors
        )
        return ResponseEntity.badRequest().body(body)
    }


    /**
     * Handles the custom ResourceNotFoundException.
     * Triggered when a service layer cannot find a requested resource (e.g., by ID).
     * @param ex The custom exception containing a specific error message.
     * @return A ResponseEntity with a 404 Not Found status and a clear error message.
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<Map<String, Any>> {
        val body: Map<String, Any> = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Not Found",
            "message" to (ex.message ?: "Resource not found.")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }
}