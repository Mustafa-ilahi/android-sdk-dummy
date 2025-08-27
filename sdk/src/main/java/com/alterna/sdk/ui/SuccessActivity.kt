package com.alterna.sdk.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.R

/**
 * Success screen after WPS validation
 * Shows receipt and transaction status
 */
class SuccessActivity : AppCompatActivity() {
    
    private lateinit var tvTransactionId: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvReceipt: TextView
    private lateinit var btnGoToHomepage: Button
    private lateinit var btnExit: Button
    private val sdk = SDK.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        
        setupViews()
        displayTransactionDetails()
    }
    
    private fun setupViews() {
        tvTransactionId = findViewById(R.id.tvTransactionId)
        tvStatus = findViewById(R.id.tvStatus)
        tvMessage = findViewById(R.id.tvMessage)
        tvReceipt = findViewById(R.id.tvReceipt)
        btnGoToHomepage = findViewById(R.id.btnGoToHomepage)
        btnExit = findViewById(R.id.btnExit)
        
        btnGoToHomepage.setOnClickListener {
            // Go to homepage (Display user details)
            val intent = Intent(this, UserDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        
        btnExit.setOnClickListener {
            sdk.exitSDK()
            finish()
        }
    }
    
    private fun displayTransactionDetails() {
        val transactionId = intent.getStringExtra("transaction_id") ?: "Unknown"
        
        tvTransactionId.text = "Transaction ID: $transactionId"
        tvStatus.text = "Status: Processing"
        tvMessage.text = "Your withdrawal request has been submitted successfully and is being processed."
        
        // Simulate transaction details - in real app this would come from backend
        tvReceipt.text = """
            Receipt Details:
            -----------------
            Transaction ID: $transactionId
            Amount: $500.00
            Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}
            Type: Withdrawal
            Status: Processing
            
            Please wait while we process your request.
            You will receive a confirmation once completed.
        """.trimIndent()
    }
}
