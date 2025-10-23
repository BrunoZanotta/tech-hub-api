package br.com.techhub.api.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Default error payload")
data class ErrorResponse(
    @Schema(example = "2025-10-21T13:46:49.090Z")
    val timestamp: Instant = Instant.now(),

    @Schema(example = "400")
    val status: Int,

    @Schema(example = "Bad Request")
    val error: String,

    @Schema(example = "The 'name' field must not be blank.")
    val message: String? = null,

    @Schema(example = "/frameworks")
    val path: String
)