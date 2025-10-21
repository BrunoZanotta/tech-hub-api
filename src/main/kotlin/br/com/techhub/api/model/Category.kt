package br.com.techhub.api.model

/**
 * Defines the standardized list of categories for classifying technology frameworks.
 *
 * Using an enum ensures type safety and consistency, preventing free-text entry errors
 * when categorizing a framework.
 */
enum class Category {
    WEB_AUTOMATION,
    MOBILE_AUTOMATION,
    API_TESTING,
    PERFORMANCE_TESTING,
    BACKEND_DEVELOPMENT,
    FRONTEND_DEVELOPMENT
}