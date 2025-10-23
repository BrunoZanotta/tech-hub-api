package br.com.techhub.api

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { it.toItem() }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.badRequest("Validation failed", errors, request.servletPath))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations.map {
            FieldErrorItem(
                field = it.propertyPath.toString(),
                message = it.message
            )
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.badRequest("Validation failed", errors, request.servletPath))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.badRequest("Malformed JSON", emptyList(), request.servletPath))
    }
}

private fun FieldError.toItem() = FieldErrorItem(field = this.field, message = this.defaultMessage ?: "Invalid")

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val code: String,
    val message: String,
    val errors: List<FieldErrorItem>? = null,
    val path: String? = null
) {
    companion object {
        fun badRequest(message: String, errors: List<FieldErrorItem>?, path: String?) =
            ErrorResponse(code = "BAD_REQUEST", message = message, errors = errors?.ifEmpty { null }, path = path)
    }
}

data class FieldErrorItem(
    val field: String,
    val message: String
)