package com.alterna.sdk.models

/**
 * Transaction model for user transaction history
 */
data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val status: TransactionStatus,
    val timestamp: Long,
    val description: String
)

enum class TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}
