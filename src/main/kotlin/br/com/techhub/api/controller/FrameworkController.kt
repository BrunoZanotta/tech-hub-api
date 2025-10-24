package br.com.techhub.api.controller

import br.com.techhub.api.dto.FrameworkRequestDTO
import br.com.techhub.api.dto.FrameworkResponseDTO
import br.com.techhub.api.exception.ErrorResponse
import br.com.techhub.api.service.FrameworkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.beans.propertyeditors.StringTrimmerEditor

@RestController
@RequestMapping("/frameworks", produces = [APPLICATION_JSON_VALUE])
@Tag(name = "Frameworks", description = "Endpoints for managing technology frameworks")
@Validated
class FrameworkController(
    private val frameworkService: FrameworkService
)
{
    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.registerCustomEditor(String::class.java, StringTrimmerEditor(true))
    }

    @Operation(
        operationId = "createFramework",
        summary = "Create framework",
        description = "Registers a new framework in the repository.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = Schema(implementation = FrameworkRequestDTO::class)
            )]
        )
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Created",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FrameworkResponseDTO::class)
                )],
                headers = [Header(
                    name = "Location",
                    description = "URI of the created resource",
                    schema = Schema(type = "string")
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Resource already exists",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun createFramework(
        @Valid @RequestBody request: FrameworkRequestDTO
    ): ResponseEntity<FrameworkResponseDTO> {
        val created = frameworkService.createFramework(request)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id)
            .toUri()
        return ResponseEntity.created(location).body(FrameworkResponseDTO(created))
    }

    @Operation(
        operationId = "listFrameworks",
        summary = "List frameworks",
        description = "Returns all frameworks."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = FrameworkResponseDTO::class))
                )]
            )
        ]
    )
    @GetMapping
    fun listFrameworks(): ResponseEntity<List<FrameworkResponseDTO>> {
        val items = frameworkService.getAllFrameworks().map { FrameworkResponseDTO(it) }
        // Futuro: headers de paginação (X-Total-Count), Cache-Control/ETag
        return ResponseEntity.ok(items)
    }

    @Operation(
        operationId = "searchFrameworksByName",
        summary = "Search frameworks by name",
        description = "Case-insensitive, partial match. The parameter must not be blank and must not be only digits."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = FrameworkResponseDTO::class))
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    @GetMapping(params = ["name"])
    fun searchFrameworksByName(
        @Parameter(
            description = "Case-insensitive, partial match",
            required = true,
            schema = Schema(
                type = "string",
                minLength = 1,
                // Pelo menos um caractere não numérico: rejeita "123"
                pattern = ".*\\D.*",
                example = "spring"
            )
        )
        @RequestParam(name = "name", required = true)
        @NotBlank(message = "The 'name' parameter cannot be blank.")
        @Pattern(
            regexp = ".*\\D.*",
            message = "The 'name' parameter cannot consist only of numbers."
        )
        name: String
    ): ResponseEntity<List<FrameworkResponseDTO>> {
        val results = frameworkService.findByName(name).map { FrameworkResponseDTO(it) }
        return ResponseEntity.ok(results)
    }

    @Operation(
        operationId = "updateFrameworkById",
        summary = "Update framework",
        description = "Updates an existing framework by id."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FrameworkResponseDTO::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Framework not found",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    @PutMapping("/{id}", consumes = [APPLICATION_JSON_VALUE])
    fun updateFrameworkById(
        @PathVariable @Positive(message = "The 'id' must be a positive number.") id: Long,
        @Valid @RequestBody request: FrameworkRequestDTO
    ): ResponseEntity<FrameworkResponseDTO> {
        val updated = frameworkService.updateFramework(id, request)
        return ResponseEntity.ok(FrameworkResponseDTO(updated))
    }

    @Operation(
        operationId = "deleteFrameworkById",
        summary = "Delete framework",
        description = "Deletes an existing framework by id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "No Content"),
            ApiResponse(
                responseCode = "404",
                description = "Framework not found",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFrameworkById(@PathVariable @Positive(message = "The 'id' must be a positive number.") id: Long) {
        frameworkService.deleteFramework(id)
    }
}