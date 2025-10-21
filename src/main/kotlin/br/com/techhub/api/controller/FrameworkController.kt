package br.com.techhub.api.controller

import br.com.techhub.api.dto.FrameworkRequestDTO
import br.com.techhub.api.dto.FrameworkResponseDTO
import br.com.techhub.api.exception.ResourceNotFoundException
import br.com.techhub.api.service.FrameworkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * Controller responsible for handling HTTP requests related to frameworks.
 * Exposes endpoints to create, retrieve, update, and delete frameworks.
 */
@RestController
@RequestMapping("/frameworks")
@Tag(name = "Frameworks", description = "Endpoints for managing technology frameworks")
@Validated
class FrameworkController(private val frameworkService: FrameworkService) {

    /**
     * Creates a new framework in the repository.
     * @param request The DTO containing the data for the new framework.
     * @return A ResponseEntity with the created framework and a 201 Created status code.
     */
    @Operation(summary = "Add a new framework", description = "Registers a new framework in the repository.")
    @PostMapping
    fun addFramework(@Valid @RequestBody request: FrameworkRequestDTO): ResponseEntity<FrameworkResponseDTO> {
        val createdFramework = frameworkService.createFramework(request)
        val responseDTO = FrameworkResponseDTO(createdFramework)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO)
    }

    /**
     * Returns a list of all registered frameworks.
     * @return A list of all frameworks.
     */
    @Operation(summary = "List all frameworks", description = "Returns a list of all registered frameworks.")
    @GetMapping
    fun getAllFrameworks(): List<FrameworkResponseDTO> {
        return frameworkService.getAllFrameworks().map { framework ->
            FrameworkResponseDTO(framework)
        }
    }

    /**
     * Finds frameworks by a given name.
     * @param name The text to be searched in the framework names (partial, case-insensitive search).
     * @return A list of frameworks matching the search criteria.
     */
    @Operation(summary = "Find frameworks by name", description = "Returns a list of frameworks whose name contains the searched text.")
    @GetMapping(params = ["name"])
    fun findFrameworksByName(
        @RequestParam
        @NotBlank(message = "The 'name' parameter cannot be blank.")
        @Pattern(regexp = ".*\\D.*", message = "The 'name' parameter cannot consist only of numbers.")
        name: String
    ): List<FrameworkResponseDTO> {
        return frameworkService.findByName(name).map { framework ->
            FrameworkResponseDTO(framework)
        }
    }

    /**
     * Updates an existing framework based on its ID.
     * @param id The unique ID of the framework to be updated.
     * @param request The DTO with the updated framework data.
     * @return A ResponseEntity with the updated framework (200 OK) or a 404 Not Found if the ID does not exist.
     * @throws ResourceNotFoundException if the framework ID is not found.
     */
    @Operation(summary = "Update an existing framework", description = "Replaces all data for a framework specified by its ID.")
    @PutMapping("/{id}")
    fun updateFramework(
        @PathVariable id: Long,
        @Valid @RequestBody request: FrameworkRequestDTO
    ): ResponseEntity<FrameworkResponseDTO> {
        val updatedFramework = frameworkService.updateFramework(id, request)
        val responseDTO = FrameworkResponseDTO(updatedFramework)
        return ResponseEntity.ok(responseDTO)
    }

    /**
     * Removes a framework from the repository by its ID.
     * @param id The unique ID of the framework to be removed.
     * @return An empty ResponseEntity with a 204 No Content status.
     * @throws ResourceNotFoundException if the framework ID is not found.
     */
    @Operation(summary = "Delete an existing framework", description = "Removes a framework from the repository by its ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFramework(@PathVariable id: Long): ResponseEntity<Void> {
        frameworkService.deleteFramework(id)
        return ResponseEntity.noContent().build()
    }
}