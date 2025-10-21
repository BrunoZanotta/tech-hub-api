package br.com.techhub.api.dto

import br.com.techhub.api.model.Category
import br.com.techhub.api.model.Framework
import br.com.techhub.api.model.Language
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

/**
 * Data Transfer Object for creating or updating a Framework.
 * This class defines the validation rules for incoming data.
 */
data class FrameworkRequestDTO(
    @field:NotBlank(message = "The name cannot be blank.")
    @field:Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters.")
    @Schema(example = "Playwright")
    val name: String,

    @field:NotBlank(message = "The current version cannot be blank.")
    @Schema(example = "1.45.0")
    val currentVersion: String
)

/**
 * Data Transfer Object for representing a Framework in API responses.
 * This class structures the data that is sent back to the client.
 */
data class FrameworkResponseDTO(
    val id: Long,
    val name: String,
    val currentVersion: String
) {
    /**
     * Secondary constructor to conveniently map a Framework model entity
     * to its corresponding response DTO.
     *
     * @param framework The internal Framework model object.
     */
    constructor(framework: Framework) : this(
        id = framework.id,
        name = framework.name,
        currentVersion = framework.currentVersion
    )
}