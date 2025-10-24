package br.com.techhub.api.exception

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "ErrorResponse", description = "Standard API error payload")
data class ErrorResponse(
    @Schema(example = "400")
    val status: Int,

    @Schema(example = "Bad Request")
    val error: String,

    @Schema(example = "The 'name' parameter cannot be blank.")
    val message: String
)