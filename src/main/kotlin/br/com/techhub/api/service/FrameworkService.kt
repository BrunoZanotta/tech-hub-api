package br.com.techhub.api.service

import br.com.techhub.api.dto.FrameworkRequestDTO
import br.com.techhub.api.exception.ResourceConflictException
import br.com.techhub.api.exception.ResourceNotFoundException
import br.com.techhub.api.model.Framework
import br.com.techhub.api.model.frameworkIdCounter
import org.springframework.stereotype.Service

@Service
class FrameworkService {

    private val frameworks = mutableListOf<Framework>()

    fun createFramework(request: FrameworkRequestDTO): Framework {
        val existsSameNameAndVersion = frameworks.any {
            it.name.equals(request.name, ignoreCase = true) &&
                    it.currentVersion == request.currentVersion
        }
        if (existsSameNameAndVersion) {
            throw ResourceConflictException(
                "Framework with name '${request.name}' and version '${request.currentVersion}' already exists."
            )
        }

        val newFramework = Framework(
            id = frameworkIdCounter.incrementAndGet(),
            name = request.name,
            currentVersion = request.currentVersion
        )
        frameworks.add(newFramework)
        return newFramework
    }

    fun getAllFrameworks(): List<Framework> = frameworks

    fun findByName(name: String): List<Framework> =
        frameworks.filter { it.name.contains(name, ignoreCase = true) }

    fun findById(id: Long): Framework =
        frameworks.find { it.id == id }
            ?: throw ResourceNotFoundException("Framework with ID $id not found.")

    fun updateFramework(id: Long, request: FrameworkRequestDTO): Framework {
        val existing = findById(id)

        val duplicate = frameworks.any {
            it.id != id &&
                    it.name.equals(request.name, ignoreCase = true) &&
                    it.currentVersion == request.currentVersion
        }
        if (duplicate) {
            throw ResourceConflictException(
                "Framework with name '${request.name}' and version '${request.currentVersion}' already exists."
            )
        }

        val updated = Framework(
            id = existing.id,
            name = request.name,
            currentVersion = request.currentVersion
        )
        frameworks[frameworks.indexOf(existing)] = updated
        return updated
    }

    fun deleteFramework(id: Long) {
        val toDelete = findById(id)
        frameworks.remove(toDelete)
    }
}