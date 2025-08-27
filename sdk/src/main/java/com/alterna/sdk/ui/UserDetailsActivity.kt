package com.alterna.sdk.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.R
import com.alterna.sdk.models.Transaction
import com.alterna.sdk.models.TransactionStatus
import com.alterna.sdk.models.TransactionType

/**
 * Display user details screen
 * Shows salary, withdrawal limits, eligibility, transaction history
 */
class UserDetailsActivity : AppCompatActivity() {
    
    private lateinit var tvUsername: TextView
    private lateinit var tvSalary: TextView
    private lateinit var tvWithdrawalLimit: TextView
    private lateinit var tvEligibility: TextView
    private lateinit var tvTransactionHistory: TextView
    private lateinit var btnWithdrawal: Button
    private lateinit var btnExit: Button
    private lateinit var btnLogout: Button
    private val sdk = SDK.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        
        setupViews()
        displayUserDetails()
        displayTransactionHistory()
    }
    
    private fun setupViews() {
        tvUsername = findViewById(R.id.tvUsername)
        tvSalary = findViewById(R.id.tvSalary)
        tvWithdrawalLimit = findViewById(R.id.tvWithdrawalLimit)
        tvEligibility = findViewById(R.id.tvEligibility)
        tvTransactionHistory = findViewById(R.id.tvTransactionHistory)
        btnWithdrawal = findViewById(R.id.btnWithdrawal)
        btnExit = findViewById(R.id.btnExit)
        btnLogout = findViewById(R.id.btnLogout)
        
        btnWithdrawal.setOnClickListener {
            // Navigate to withdrawal page
            startActivity(Intent(this, WithdrawalActivity::class.java))
        }
        
        btnExit.setOnClickListener {
            sdk.exitSDK()
            finish()
        }
        
        btnLogout.setOnClickListener {
            // Clear session and go back to onboarding
            sdk.exitSDK()
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
    }
    
    private fun displayUserDetails() {
        val session = sdk.getCurrentSession()
        if (session != null) {
            tvUsername.text = "Welcome, ${session.username}"
            
            // Simulate user data - in real app this would come from backend
            tvSalary.text = "Salary: $5,000.00"
            tvWithdrawalLimit.text = "Withdrawal Limit: $1,000.00"
            
            // Check eligibility
            if (sdk.isUserEligible()) {
                tvEligibility.text = "Status: Eligible ✓"
                tvEligibility.setTextColor(getColor(android.R.color.holo_green_dark))
            } else {
                tvEligibility.text = "Status: Not Eligible ✗"
                tvEligibility.setTextColor(getColor(android.R.color.holo_red_dark))
                btnWithdrawal.isEnabled = false
                Toast.makeText(this, "User not eligible for withdrawal", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun displayTransactionHistory() {
        // Simulate transaction history - in real app this would come from backend
        val transactions = listOf(
            Transaction(
                id = "TXN_001",
                amount = 500.0,
                type = TransactionType.WITHDRAWAL,
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                description = "ATM Withdrawal"
            ),
            Transaction(
                id = "TXN_002",
                amount = 1000.0,
                type = TransactionType.DEPOSIT,
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                description = "Salary Deposit"
            )
        )
        
        val transactionText = transactions.joinToString("\n") { transaction ->
            "${transaction.description}: $${transaction.amount} (${transaction.status})"
        }
        
        tvTransactionHistory.text = "Recent Transactions:\n$transactionText"
    }
}
