package com.alterna.sdk.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alterna.sdk.SDK
import com.alterna.sdk.R
import com.alterna.sdk.models.User

/**
 * Step 1: Onboarding/Login Activity
 * Creates a session for user
 */
class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnExit: Button
    private val sdk = SDK.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        setupViews()
    }
    
    private fun setupViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnExit = findViewById(R.id.btnExit)
        
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Simulate login - in real app this would call backend
                val user = User(
                    id = "USER_${System.currentTimeMillis()}",
                    username = username,
                    email = "$username@example.com",
                    salary = 5000.0,
                    withdrawalLimit = 1000.0,
                    isEligible = true
                )
                
                // Create user session
                val session = sdk.createUserSession(user)
                
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                
                // Navigate to user details
                startActivity(Intent(this, UserDetailsActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnExit.setOnClickListener {
            sdk.exitSDK()
            finish()
        }
    }
}
