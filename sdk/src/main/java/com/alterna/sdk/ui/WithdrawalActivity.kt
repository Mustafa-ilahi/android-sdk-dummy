package com.alterna.sdk.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.R
import com.alterna.sdk.callbacks.WithdrawalCallback
import com.alterna.sdk.wps.WPSHandler

/**
 * Withdrawal page
 * Shows withdrawal limits, consent popups, and exposes callback to WPS
 */
class WithdrawalActivity : AppCompatActivity(), WithdrawalCallback {
    
    private lateinit var tvWithdrawalLimit: TextView
    private lateinit var tvDailyLimit: TextView
    private lateinit var etAmount: EditText
    private lateinit var btnWithdraw: Button
    private lateinit var btnBack: Button
    private lateinit var btnPrivacyPolicy: Button
    private lateinit var btnTerms: Button
    private val sdk = SDK.getInstance()
    private val wpsHandler = WPSHandler.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdrawal)
        
        setupViews()
        displayWithdrawalLimits()
    }
    
    private fun setupViews() {
        tvWithdrawalLimit = findViewById(R.id.tvWithdrawalLimit)
        tvDailyLimit = findViewById(R.id.tvDailyLimit)
        etAmount = findViewById(R.id.etAmount)
        btnWithdraw = findViewById(R.id.btnWithdraw)
        btnBack = findViewById(R.id.btnBack)
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy)
        btnTerms = findViewById(R.id.btnTerms)
        
        btnWithdraw.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            
            if (amount != null && amount > 0) {
                if (amount <= 1000.0) { // Withdrawal limit
                    showConsentDialog(amount)
                } else {
                    Toast.makeText(this, "Amount exceeds withdrawal limit", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnBack.setOnClickListener {
            finish()
        }
        
        btnPrivacyPolicy.setOnClickListener {
            showPrivacyPolicyDialog()
        }
        
        btnTerms.setOnClickListener {
            showTermsDialog()
        }
    }
    
    private fun displayWithdrawalLimits() {
        tvWithdrawalLimit.text = "Available Withdrawal Limit: $1,000.00"
        tvDailyLimit.text = "Daily Limit: $500.00"
    }
    
    private fun showConsentDialog(amount: Double) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Withdrawal")
            .setMessage("Do you agree to the terms and conditions for withdrawing $${amount}?")
            .setPositiveButton("Agree") { _, _ ->
                showPrivacyPolicyPopup(amount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPrivacyPolicyPopup(amount: Double) {
        AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage("By proceeding, you agree to our privacy policy and data processing terms.")
            .setPositiveButton("Accept") { _, _ ->
                // Expose callback to WPS for their side of logic
                initiateWithdrawal(amount)
            }
            .setNegativeButton("Decline", null)
            .show()
    }
    
    private fun showPrivacyPolicyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Privacy Policy")
            .setMessage("Your privacy is important to us. We collect and process your data according to our privacy policy.")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showTermsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Terms & Conditions")
            .setMessage("By using this service, you agree to our terms and conditions.")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun initiateWithdrawal(amount: Double) {
        // This callback is exposed to WPS for their side of logic
        onWithdrawalRequested(amount) { success, error ->
            if (success) {
                // WPS validation successful, proceed to success screen
                val transactionId = "TXN_${System.currentTimeMillis()}"
                sdk.showSuccessScreen(this, transactionId)
                finish()
            } else {
                Toast.makeText(this, "Withdrawal failed: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // WithdrawalCallback implementation
    override fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit) {
        // Simulate WPS validation process
        val session = sdk.getCurrentSession()
        if (session != null) {
            // WPS validates user (password/PIN)
            wpsHandler.validateUser(session.userId, "dummy_password") { isValid, error ->
                if (isValid) {
                    // WPS checks eligibility
                    wpsHandler.checkEligibility(session.userId, amount) { isEligible, eligibilityError ->
                        if (isEligible) {
                            // WPS processes withdrawal
                            wpsHandler.processWithdrawal(session.userId, amount) { success, transactionId ->
                                if (success) {
                                    callback(true, null)
                                } else {
                                    callback(false, "Transaction processing failed")
                                }
                            }
                        } else {
                            callback(false, eligibilityError)
                        }
                    }
                } else {
                    callback(false, error)
                }
            }
        } else {
            callback(false, "No active session")
        }
    }
    
    override fun onWithdrawalValidated(transactionId: String, success: Boolean) {
        // Called when WPS validates withdrawal
    }
    
    override fun onWithdrawalCompleted(transactionId: String, amount: Double) {
        // Called when WPS completes withdrawal
        Toast.makeText(this, "Withdrawal completed: $${amount}", Toast.LENGTH_SHORT).show()
    }
}
