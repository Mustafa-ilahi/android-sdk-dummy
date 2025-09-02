package com.alterna.sdk

import android.content.Context
import android.content.Intent
import com.alterna.sdk.callbacks.SDKCallback
import com.alterna.sdk.callbacks.WithdrawalCallback
import com.alterna.sdk.models.User
import com.alterna.sdk.models.UserSession
import com.alterna.sdk.ui.OnboardingActivity
import com.alterna.sdk.ui.UserDetailsActivity
import com.alterna.sdk.ui.WithdrawalActivity
import com.alterna.sdk.ui.SuccessActivity

/**
 * Alterna SDK - Main entry point for integrating Alterna services
 * 
 * This SDK provides a complete solution for integrating Alterna's user onboarding,
 * authentication, and withdrawal services into Android applications.
 * 
 * ## Features
 * - User onboarding and authentication
 * - User session management
 * - Withdrawal processing with WPS integration
 * - Success screen handling
 * 
 * ## Usage
 * ```kotlin
 * val sdk = SDK.getInstance()
 * sdk.initialize(context, callback)
 * sdk.startOnboarding(context)
 * ```
 * 
 * @since 1.0.0
 * @author Alterna Team
 */
class SDK private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: SDK? = null
        
        /**
         * Get the singleton instance of the SDK
         * 
         * @return The SDK instance
         */
        fun getInstance(): SDK {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SDK().also { INSTANCE = it }
            }
        }
    }
    
    private var currentSession: UserSession? = null
    private var sdkCallback: SDKCallback? = null
    private var withdrawalCallback: WithdrawalCallback? = null
    
    /**
     * Initialize the SDK with configuration
     * 
     * This method must be called before using any other SDK functionality.
     * It sets up the SDK with the provided context and callback.
     * 
     * @param context The application context
     * @param callback The SDK callback for handling events
     */
    fun initialize(context: Context, callback: SDKCallback) {
        this.sdkCallback = callback
        // SDK initialization logic here
    }
    
    /**
     * Start the onboarding/login flow (Step 1)
     * 
     * Launches the onboarding activity where users can log in or create an account.
     * This is typically the first method called after SDK initialization.
     * 
     * @param context The context to start the activity from
     */
    fun startOnboarding(context: Context) {
        val intent = Intent(context, OnboardingActivity::class.java)
        context.startActivity(intent)
    }
    
    /**
     * Create a user session after successful login
     */
    fun createUserSession(user: User): UserSession {
        currentSession = UserSession(
            userId = user.id,
            username = user.username,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
        return currentSession!!
    }
    
    /**
     * Get current user session
     */
    fun getCurrentSession(): UserSession? = currentSession
    
    /**
     * Display user details screen
     */
    fun showUserDetails(context: Context) {
        if (currentSession == null) {
            sdkCallback?.onError("No active session. Please login first.")
            return
        }
        
        val intent = Intent(context, UserDetailsActivity::class.java)
        context.startActivity(intent)
    }
    
    /**
     * Show withdrawal page
     */
    fun showWithdrawalPage(context: Context, callback: WithdrawalCallback) {
        if (currentSession == null) {
            sdkCallback?.onError("No active session. Please login first.")
            return
        }
        
        this.withdrawalCallback = callback
        val intent = Intent(context, WithdrawalActivity::class.java)
        context.startActivity(intent)
    }
    
    /**
     * Handle withdrawal request from WPS
     */
    fun handleWithdrawalRequest(amount: Double, callback: (Boolean, String?) -> Unit) {
        withdrawalCallback?.onWithdrawalRequested(amount, callback)
    }
    
    /**
     * Show success screen after WPS validation
     */
    fun showSuccessScreen(context: Context, transactionId: String) {
        val intent = Intent(context, SuccessActivity::class.java).apply {
            putExtra("transaction_id", transactionId)
        }
        context.startActivity(intent)
    }
    
    /**
     * Exit SDK and clear session
     */
    fun exitSDK() {
        currentSession = null
        sdkCallback?.onSDKExited()
    }
    
    /**
     * Check if user is eligible for withdrawal
     */
    fun isUserEligible(): Boolean {
        // Eligibility check logic here
        return currentSession?.isActive == true
    }
}
