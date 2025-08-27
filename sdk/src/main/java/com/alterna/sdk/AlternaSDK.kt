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
 * Main SDK class that manages the user flow
 */
class SDK private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: SDK? = null
        
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
     */
    fun initialize(context: Context, callback: SDKCallback) {
        this.sdkCallback = callback
        // SDK initialization logic here
    }
    
    /**
     * Start the onboarding/login flow (Step 1)
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
