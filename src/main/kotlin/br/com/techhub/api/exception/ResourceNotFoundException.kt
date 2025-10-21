package br.com.techhub.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * A custom runtime exception thrown when a requested resource is not found in the system.
 *
 * This exception is typically handled by a global exception handler to produce a
 * 404 Not Found HTTP response, making the API's error handling clear and consistent.
 *
 * @param message A descriptive message explaining why the resource was not found (e.g., "Framework with ID 99 not found.").
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(message: String) : RuntimeException(message)