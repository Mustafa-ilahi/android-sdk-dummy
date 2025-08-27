package com.alterna.sdk.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.callbacks.SDKCallback
import com.alterna.sdk.callbacks.WithdrawalCallback

class MainActivity : AppCompatActivity(), SDKCallback, WithdrawalCallback {
    
    private lateinit var btnStartSDK: Button
    private lateinit var btnShowUserDetails: Button
    private lateinit var btnShowWithdrawal: Button
    private lateinit var btnExitSDK: Button
    private val sdk = SDK.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupViews()
        initializeSDK()
    }
    
    private fun setupViews() {
        btnStartSDK = findViewById(R.id.btnStartSDK)
        btnShowUserDetails = findViewById(R.id.btnShowUserDetails)
        btnShowWithdrawal = findViewById(R.id.btnShowWithdrawal)
        btnExitSDK = findViewById(R.id.btnExitSDK)
        
        btnStartSDK.setOnClickListener {
            sdk.startOnboarding(this)
        }
        
        btnShowUserDetails.setOnClickListener {
            sdk.showUserDetails(this)
        }
        
        btnShowWithdrawal.setOnClickListener {
            sdk.showWithdrawalPage(this, this)
        }
        
        btnExitSDK.setOnClickListener {
            sdk.exitSDK()
        }
    }
    
    private fun initializeSDK() {
        sdk.initialize(this, this)
        Toast.makeText(this, "SDK initialized successfully!", Toast.LENGTH_SHORT).show()
    }
    
    // SDKCallback implementation
    override fun onSDKInitialized() {
        Toast.makeText(this, "SDK initialized", Toast.LENGTH_SHORT).show()
    }
    
    override fun onError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
    
    override fun onUserLoggedIn(userId: String) {
        Toast.makeText(this, "User logged in: $userId", Toast.LENGTH_SHORT).show()
    }
    
    override fun onUserLoggedOut() {
        Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show()
    }
    
    override fun onSDKExited() {
        Toast.makeText(this, "SDK exited", Toast.LENGTH_SHORT).show()
    }
    
    // WithdrawalCallback implementation
    override fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit) {
        Toast.makeText(this, "Withdrawal requested: $${amount}", Toast.LENGTH_SHORT).show()
        // Simulate successful withdrawal
        callback(true, null)
    }
    
    override fun onWithdrawalValidated(transactionId: String, success: Boolean) {
        Toast.makeText(this, "Withdrawal validated: $transactionId", Toast.LENGTH_SHORT).show()
    }
    
    override fun onWithdrawalCompleted(transactionId: String, amount: Double) {
        Toast.makeText(this, "Withdrawal completed: $${amount}", Toast.LENGTH_SHORT).show()
    }
}
