package br.com.techhub.api.model

import java.util.concurrent.atomic.AtomicLong

/**
 * A temporary, in-memory counter to generate unique IDs for Review instances.
 * This will be replaced by a database-generated sequence in a production environment.
 */
val reviewIdCounter = AtomicLong()

/**
 * Represents a user-submitted review for a specific technology framework.
 * This entity is associated with a Framework by its ID.
 */
data class Review(
    /** The unique identifier for the review. */
    val id: Long = reviewIdCounter.incrementAndGet(),

    /** The ID of the Framework this review is associated with. */
    val frameworkId: Long,

    /** The numerical score given to the framework, typically on a scale (e.g., 1 to 5). */
    val rating: Int,

    /** An optional text comment providing more details about the review. */
    val comment: String?,

    /** The name of the person who submitted the review. */
    val author: String
)