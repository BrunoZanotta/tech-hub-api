package br.com.techhub.api.dto

import br.com.techhub.api.model.Framework
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class FrameworkRequestDTO(

    @field:NotBlank(message = "The name cannot be blank.")
    @field:Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters.")
    @field:Pattern(
        regexp = ".*\\D.*",
        message = "The name cannot consist only of numbers."
    )
    @Schema(
        description = "Human-friendly framework name. Must contain at least one non-digit character.",
        minLength = 2,
        maxLength = 100,
        pattern = ".*\\D.*",
        example = "Playwright"
    )
    val name: String,

    @field:NotBlank(message = "The current version cannot be blank.")
    @field:Size(min = 1, max = 50, message = "The current version must be up to 50 characters.")
    @Schema(
        description = "Current version label (semantic or vendor style).",
        example = "1.48.0"
    )
    val currentVersion: String
)

data class FrameworkResponseDTO(
    @Schema(description = "Framework unique identifier", example = "42")
    val id: Long,

    @Schema(description = "Framework name", example = "Playwright")
    val name: String,

    @Schema(description = "Current version label", example = "1.48.0")
    val currentVersion: String
) {
    constructor(model: Framework) : this(
        id = model.id,
        name = model.name,
        currentVersion = model.currentVersion
    )
}