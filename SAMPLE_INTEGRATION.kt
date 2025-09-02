package com.example.myapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.callbacks.SDKCallback
import com.alterna.sdk.callbacks.WithdrawalCallback
import com.alterna.sdk.models.User

/**
 * Sample integration of Alterna SDK
 * This example shows how to integrate the SDK into your Android app
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var alternaSDK: SDK
    private lateinit var btnStartOnboarding: Button
    private lateinit var btnShowUserDetails: Button
    private lateinit var btnShowWithdrawal: Button
    private lateinit var btnExitSDK: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        initializeSDK()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        btnStartOnboarding = findViewById(R.id.btn_start_onboarding)
        btnShowUserDetails = findViewById(R.id.btn_show_user_details)
        btnShowWithdrawal = findViewById(R.id.btn_show_withdrawal)
        btnExitSDK = findViewById(R.id.btn_exit_sdk)
    }
    
    private fun initializeSDK() {
        alternaSDK = SDK.getInstance()
        alternaSDK.initialize(this, object : SDKCallback {
            override fun onSDKInitialized() {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "SDK Initialized Successfully", Toast.LENGTH_SHORT).show()
                    Log.d("AlternaSDK", "SDK initialized successfully")
                }
            }
            
            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "SDK Error: $error", Toast.LENGTH_LONG).show()
                    Log.e("AlternaSDK", "Error: $error")
                }
            }
            
            override fun onSDKExited() {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "SDK Exited", Toast.LENGTH_SHORT).show()
                    Log.d("AlternaSDK", "SDK exited")
                }
            }
        })
    }
    
    private fun setupClickListeners() {
        btnStartOnboarding.setOnClickListener {
            startOnboarding()
        }
        
        btnShowUserDetails.setOnClickListener {
            showUserDetails()
        }
        
        btnShowWithdrawal.setOnClickListener {
            showWithdrawalPage()
        }
        
        btnExitSDK.setOnClickListener {
            exitSDK()
        }
    }
    
    private fun startOnboarding() {
        try {
            alternaSDK.startOnboarding(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start onboarding: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AlternaSDK", "Failed to start onboarding", e)
        }
    }
    
    private fun showUserDetails() {
        val currentSession = alternaSDK.getCurrentSession()
        if (currentSession == null) {
            Toast.makeText(this, "No active session. Please login first.", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            alternaSDK.showUserDetails(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to show user details: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AlternaSDK", "Failed to show user details", e)
        }
    }
    
    private fun showWithdrawalPage() {
        val currentSession = alternaSDK.getCurrentSession()
        if (currentSession == null) {
            Toast.makeText(this, "No active session. Please login first.", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!alternaSDK.isUserEligible()) {
            Toast.makeText(this, "User is not eligible for withdrawal", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            alternaSDK.showWithdrawalPage(this, object : WithdrawalCallback {
                override fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit) {
                    Log.d("AlternaSDK", "Withdrawal requested for amount: $amount")
                    
                    // Simulate withdrawal processing
                    processWithdrawal(amount) { success, transactionId ->
                        callback(success, transactionId)
                    }
                }
                
                override fun onWithdrawalCompleted(transactionId: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Withdrawal completed: $transactionId", Toast.LENGTH_LONG).show()
                        Log.d("AlternaSDK", "Withdrawal completed: $transactionId")
                    }
                }
                
                override fun onWithdrawalFailed(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Withdrawal failed: $error", Toast.LENGTH_LONG).show()
                        Log.e("AlternaSDK", "Withdrawal failed: $error")
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to show withdrawal page: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AlternaSDK", "Failed to show withdrawal page", e)
        }
    }
    
    private fun processWithdrawal(amount: Double, callback: (Boolean, String?) -> Unit) {
        // Simulate API call to your backend
        Thread {
            try {
                // Simulate network delay
                Thread.sleep(2000)
                
                // Simulate successful withdrawal
                val transactionId = "TXN_${System.currentTimeMillis()}"
                Log.d("AlternaSDK", "Withdrawal processed successfully: $transactionId")
                
                callback(true, transactionId)
            } catch (e: Exception) {
                Log.e("AlternaSDK", "Withdrawal processing failed", e)
                callback(false, e.message)
            }
        }.start()
    }
    
    private fun exitSDK() {
        try {
            alternaSDK.exitSDK()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to exit SDK: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AlternaSDK", "Failed to exit SDK", e)
        }
    }
    
    // Example of creating a user session after successful login
    private fun createUserSession() {
        val user = User(
            id = "user_123",
            username = "john_doe",
            email = "john@example.com"
        )
        
        val session = alternaSDK.createUserSession(user)
        Log.d("AlternaSDK", "User session created: ${session.userId}")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up if needed
    }
}
