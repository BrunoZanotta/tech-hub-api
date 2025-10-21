package br.com.techhub.api.service

import br.com.techhub.api.dto.FrameworkRequestDTO
import br.com.techhub.api.exception.ResourceNotFoundException
import br.com.techhub.api.model.Framework
import br.com.techhub.api.model.frameworkIdCounter
import org.springframework.stereotype.Service

/**
 * Service layer responsible for the business logic related to frameworks.
 * The controller delegates tasks to this class.
 */
@Service
class FrameworkService {

    private val frameworks = mutableListOf<Framework>()

    /**
     * Creates a new framework from a DTO and saves it to the list.
     * @param request The DTO containing the data for the new framework.
     * @return The created Framework object.
     */
    fun createFramework(request: FrameworkRequestDTO): Framework {
        val newFramework = Framework(
            id = frameworkIdCounter.incrementAndGet(),
            name = request.name,
            category = request.category,
            primaryLanguage = request.primaryLanguage,
            description = request.description,
            officialSite = request.officialSite,
            currentVersion = request.currentVersion
        )
        frameworks.add(newFramework)
        return newFramework
    }

    /**
     * Returns the complete list of all registered frameworks.
     * @return A list of Framework objects.
     */
    fun getAllFrameworks(): List<Framework> {
        return frameworks
    }

    /**
     * Filters frameworks whose name contains the search text (case-insensitive).
     * @param name The text to search for within framework names.
     * @return A list of frameworks that match the search.
     */
    fun findByName(name: String): List<Framework> {
        return frameworks.filter { it.name.contains(name, ignoreCase = true) }
    }

    /**
     * Finds a specific framework by its ID.
     * @param id The ID of the framework to find.
     * @return The corresponding Framework object.
     * @throws ResourceNotFoundException if no framework with the given ID is found.
     */
    fun findById(id: Long): Framework {
        return frameworks.find { it.id == id }
            ?: throw ResourceNotFoundException("Framework with ID $id not found.")
    }

    /**
     * Updates an existing framework based on its ID.
     * @param id The ID of the framework to update.
     * @param request The DTO with the new data for the framework.
     * @return The updated Framework object.
     * @throws ResourceNotFoundException if the framework to be updated is not found.
     */
    fun updateFramework(id: Long, request: FrameworkRequestDTO): Framework {
        val existingFramework = findById(id)

        val updatedFramework = Framework(
            id = existingFramework.id,
            name = request.name,
            category = request.category,
            primaryLanguage = request.primaryLanguage,
            description = request.description,
            officialSite = request.officialSite,
            currentVersion = request.currentVersion
        )

        val index = frameworks.indexOf(existingFramework)
        frameworks[index] = updatedFramework

        return updatedFramework
    }

    /**
     * Deletes a framework by its ID.
     * @param id The ID of the framework to be deleted.
     * @throws ResourceNotFoundException if the framework is not found.
     */
    fun deleteFramework(id: Long) {
        val frameworkToDelete = findById(id)

        frameworks.remove(frameworkToDelete)
    }
}