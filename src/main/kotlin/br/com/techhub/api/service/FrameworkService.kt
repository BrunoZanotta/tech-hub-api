package br.com.techhub.api.service

import br.com.techhub.api.dto.FrameworkRequestDTO
import br.com.techhub.api.exception.ResourceNotFoundException
import br.com.techhub.api.model.Framework
import br.com.techhub.api.model.frameworkIdCounter
import org.springframework.stereotype.Service

@Service
class FrameworkService {

    private val frameworks = mutableListOf<Framework>()

    fun createFramework(request: FrameworkRequestDTO): Framework {
        val name = request.name.trim()
        val version = request.currentVersion.trim()

        if (frameworks.any { it.name.equals(name, ignoreCase = true) }) {
            throw IllegalStateException("Framework with name '$name' already exists.")
        }

        val newFramework = Framework(
            id = frameworkIdCounter.incrementAndGet(),
            name = name,
            currentVersion = version
        )
        frameworks.add(newFramework)
        return newFramework
    }

    fun getAllFrameworks(): List<Framework> = frameworks.toList()

    fun findByName(name: String): List<Framework> {
        val q = name.trim()
        return frameworks.filter { it.name.contains(q, ignoreCase = true) }
    }

    fun findById(id: Long): Framework =
        frameworks.find { it.id == id }
            ?: throw ResourceNotFoundException("Framework with ID $id not found.")

    fun updateFramework(id: Long, request: FrameworkRequestDTO): Framework {
        val existing = findById(id)

        val name = request.name.trim()
        val version = request.currentVersion.trim()

        if (frameworks.any { it.id != id && it.name.equals(name, ignoreCase = true) }) {
            throw IllegalStateException("Framework with name '$name' already exists.")
        }

        val updated = existing.copy(
            name = name,
            currentVersion = version
        )

        val idx = frameworks.indexOf(existing)
        frameworks[idx] = updated
        return updated
    }

    fun deleteFramework(id: Long) {
        val toDelete = findById(id)
        frameworks.remove(toDelete)
    }
}