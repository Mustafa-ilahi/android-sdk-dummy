package com.alterna.sdk.models

/**
 * User session model for managing active sessions
 */
data class UserSession(
    val userId: String,
    val username: String,
    val isActive: Boolean,
    val createdAt: Long
)
