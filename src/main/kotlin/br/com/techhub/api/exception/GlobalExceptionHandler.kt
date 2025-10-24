package br.com.techhub.api.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private fun badRequest(message: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", message))

    private fun notFound(message: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", message))

    private fun conflict(message: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict", message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult
            .allErrors
            .joinToString("; ") {
                if (it is FieldError) "${it.field}: ${it.defaultMessage}" else it.defaultMessage ?: "Validation error"
            }

        log.debug("Validation error at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = ex.constraintViolations.joinToString("; ") { it.toReadableMessage() }
        log.debug("Constraint violation at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = "The '${ex.parameterName}' parameter is required and cannot be null or blank."
        log.debug("Missing request param at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class, TypeMismatchException::class])
    fun handleTypeMismatch(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {

        val pair: Pair<String, String> = when (ex) {
            is MethodArgumentTypeMismatchException ->
                Pair(ex.name, ex.requiredType?.simpleName ?: "required type")

            is TypeMismatchException ->
                Pair(ex.propertyName ?: "parameter", ex.requiredType?.simpleName ?: "required type")

            else ->
                Pair("parameter", "required type")
        }

        val (name, expected) = pair
        val message = "The '$name' parameter must be of type $expected."
        log.debug("Type mismatch at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = ex.allErrors.joinToString("; ") {
            if (it is FieldError) "${it.field}: ${it.defaultMessage}" else it.defaultMessage ?: "Validation error"
        }
        log.debug("Bind error at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val cause = ex.mostSpecificCause
        val message = when (cause) {
            is JsonParseException -> "Malformed JSON in request body."
            is InvalidFormatException -> {
                val path = cause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                "Invalid value for field '$path'. Expected type: ${cause.targetType.simpleName}."
            }
            is MismatchedInputException -> {
                if (cause.path.isNullOrEmpty()) "Request body is missing or has an unexpected structure."
                else {
                    val path = cause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                    "Invalid or missing value for field '$path'."
                }
            }
            else -> {
                val raw = cause?.message ?: ex.message.orEmpty()
                when {
                    raw.contains("name", ignoreCase = true) ->
                        "The 'name' field is required and cannot be null or blank."
                    raw.contains("currentVersion", ignoreCase = true) ->
                        "The 'currentVersion' field is required and cannot be null or blank."
                    else -> "Invalid request body."
                }
            }
        }

        log.debug("Unreadable body at {}: {}", request.requestURI, message)
        return badRequest(message)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val supported = ex.supportedHttpMethods?.joinToString(", ") ?: "N/A"
        val message = "HTTP method '${ex.method}' is not supported. Supported methods: $supported."
        log.debug("Method not supported at {}: {}", request.requestURI, message)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed", message))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.debug("Not found at {}: {}", request.requestURI, ex.message)
        return notFound(ex.message ?: "Resource not found.")
    }

    @ExceptionHandler(ResourceConflictException::class)
    fun handleConflict(ex: ResourceConflictException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.debug("Conflict at {}: {}", request.requestURI, ex.message)
        return conflict(ex.message ?: "Conflict.")
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error at {}: {}", request.requestURI, ex.message, ex)
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred."
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}

private fun ConstraintViolation<*>.toReadableMessage(): String {
    val path = this.propertyPath?.toString()?.substringAfterLast('.') ?: "value"
    return "$path: ${this.message}"
}