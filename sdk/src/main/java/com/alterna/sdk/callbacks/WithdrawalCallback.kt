package com.alterna.sdk.callbacks

/**
 * Callback interface for withdrawal operations
 */
interface WithdrawalCallback {
    /**
     * Called when user requests a withdrawal
     * This callback is exposed to WPS for their side of logic
     */
    fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit)
    
    /**
     * Called when withdrawal is validated by WPS
     */
    fun onWithdrawalValidated(transactionId: String, success: Boolean)
    
    /**
     * Called when withdrawal is completed
     */
    fun onWithdrawalCompleted(transactionId: String, amount: Double)
}
