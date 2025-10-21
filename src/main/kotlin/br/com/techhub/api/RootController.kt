package br.com.techhub.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {
    @GetMapping("/")
    fun root(): Map<String, Any> = mapOf(
        "app" to "tech-hub-api",
        "status" to "UP",
        "health" to "/actuator/health",
        "swagger" to "/swagger-ui",
        "docs" to "/v3/api-docs"
    )
}
