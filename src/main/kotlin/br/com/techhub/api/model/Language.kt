package br.com.techhub.api.model

/**
 * Defines a standardized list of primary programming languages associated with a technology framework.
 *
 * Using an enum ensures type safety and consistency when classifying frameworks,
 * preventing free-text entry errors.
 */
enum class Language {
    KOTLIN,
    JAVA,
    JAVASCRIPT,
    TYPESCRIPT,
    PYTHON,
    GO,
    CSHARP
}