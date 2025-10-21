package br.com.techhub.api.model

import io.swagger.v3.oas.annotations.media.Schema
import java.util.concurrent.atomic.AtomicLong

/**
 * A temporary, in-memory counter to generate unique IDs for Framework instances.
 * This will be replaced by a database-generated sequence in a production environment.
 */
val frameworkIdCounter = AtomicLong()

/**
 * Represents the core domain entity for a technology framework in the Tech Hub.
 *
 * This data class serves as the internal model and is mapped to DTOs (Data Transfer Objects)
 * for communication through the API.
 */
data class Framework(
    /** The unique identifier for the framework, auto-incremented for new instances. */
    val id: Long = frameworkIdCounter.incrementAndGet(),

    /** The official name of the framework (e.g., "Spring Boot", "Playwright"). */
    @Schema(example = "Playwright")
    val name: String,

    /** The classification of the framework (e.g., BACKEND_DEVELOPMENT, WEB_AUTOMATION). */
    val category: Category,

    /** The main programming language associated with the framework (e.g., KOTLIN, JAVASCRIPT). */
    val primaryLanguage: Language,

    /** A brief summary of the framework's purpose and features. */
    @Schema(example = "Modern framework for end-to-end web automation testing.")
    val description: String,

    /** The official website URL for the framework. */
    @Schema(example = "https://playwright.dev/")
    val officialSite: String,

    /** The latest stable version of the framework. Mutable (`var`) to allow for updates. */
    @Schema(example = "1.45.0")
    var currentVersion: String
)
