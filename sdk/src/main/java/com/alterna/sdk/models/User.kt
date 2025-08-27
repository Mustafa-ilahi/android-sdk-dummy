package com.alterna.sdk.models

/**
 * User model representing a user in the system
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val salary: Double,
    val withdrawalLimit: Double,
    val isEligible: Boolean = true
)
