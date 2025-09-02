# Alterna Android SDK Integration Guide

## Overview

The Alterna Android SDK provides a complete solution for integrating Alterna's user onboarding, authentication, and withdrawal services into your Android applications.

## Features

- **User Onboarding**: Complete login and registration flow
- **Session Management**: Secure user session handling
- **Withdrawal Processing**: WPS-integrated withdrawal functionality
- **Success Handling**: Transaction completion screens

## Installation

### Option 1: GitHub Packages (Recommended)

Add the following to your project's `build.gradle` (Project level):

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/your-org/alterna-android-sdk")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
            }
        }
    }
}
```

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.alterna:sdk:1.0.0'
}
```

### Option 2: Local AAR

1. Build the SDK:
```bash
./gradlew :sdk:assembleRelease
```

2. Copy the generated AAR from `sdk/build/outputs/aar/sdk-release.aar` to your project's `libs` folder.

3. Add to your app's `build.gradle`:
```gradle
dependencies {
    implementation files('libs/sdk-release.aar')
}
```

## Quick Start

### 1. Initialize the SDK

```kotlin
import com.alterna.sdk.SDK
import com.alterna.sdk.callbacks.SDKCallback

class MainActivity : AppCompatActivity() {
    
    private lateinit var alternaSDK: SDK
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize the SDK
        alternaSDK = SDK.getInstance()
        alternaSDK.initialize(this, object : SDKCallback {
            override fun onSDKInitialized() {
                // SDK is ready to use
                Log.d("AlternaSDK", "SDK initialized successfully")
            }
            
            override fun onError(error: String) {
                // Handle SDK errors
                Log.e("AlternaSDK", "Error: $error")
            }
            
            override fun onSDKExited() {
                // SDK has been exited
                Log.d("AlternaSDK", "SDK exited")
            }
        })
    }
}
```

### 2. Start User Onboarding

```kotlin
private fun startOnboarding() {
    alternaSDK.startOnboarding(this)
}
```

### 3. Handle User Session

```kotlin
// After successful login, create a user session
val user = User(
    id = "user123",
    username = "john_doe",
    email = "john@example.com"
)

val session = alternaSDK.createUserSession(user)
```

### 4. Show User Details

```kotlin
private fun showUserDetails() {
    alternaSDK.showUserDetails(this)
}
```

### 5. Handle Withdrawals

```kotlin
import com.alterna.sdk.callbacks.WithdrawalCallback

private fun showWithdrawalPage() {
    alternaSDK.showWithdrawalPage(this, object : WithdrawalCallback {
        override fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit) {
            // Handle withdrawal request
            // Process the withdrawal with your backend
            processWithdrawal(amount) { success, transactionId ->
                callback(success, transactionId)
            }
        }
        
        override fun onWithdrawalCompleted(transactionId: String) {
            // Withdrawal completed successfully
            Log.d("AlternaSDK", "Withdrawal completed: $transactionId")
        }
        
        override fun onWithdrawalFailed(error: String) {
            // Handle withdrawal failure
            Log.e("AlternaSDK", "Withdrawal failed: $error")
        }
    })
}

private fun processWithdrawal(amount: Double, callback: (Boolean, String?) -> Unit) {
    // Your withdrawal processing logic here
    // Call callback(true, "transaction_id") on success
    // Call callback(false, "error_message") on failure
}
```

## Configuration

### AndroidManifest.xml

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### ProGuard Configuration

If you're using ProGuard, the SDK includes consumer rules that will be automatically applied. No additional configuration is needed.

## API Reference

### SDK Class

#### Methods

- `getInstance()`: Get the singleton SDK instance
- `initialize(context, callback)`: Initialize the SDK
- `startOnboarding(context)`: Start the onboarding flow
- `createUserSession(user)`: Create a user session
- `getCurrentSession()`: Get the current user session
- `showUserDetails(context)`: Show user details screen
- `showWithdrawalPage(context, callback)`: Show withdrawal page
- `handleWithdrawalRequest(amount, callback)`: Handle withdrawal request
- `showSuccessScreen(context, transactionId)`: Show success screen
- `exitSDK()`: Exit SDK and clear session
- `isUserEligible()`: Check if user is eligible for withdrawal

### Callback Interfaces

#### SDKCallback
- `onSDKInitialized()`: Called when SDK is initialized
- `onError(error: String)`: Called when an error occurs
- `onSDKExited()`: Called when SDK is exited

#### WithdrawalCallback
- `onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit)`: Called when withdrawal is requested
- `onWithdrawalCompleted(transactionId: String)`: Called when withdrawal is completed
- `onWithdrawalFailed(error: String)`: Called when withdrawal fails

### Model Classes

#### User
- `id: String`: User ID
- `username: String`: Username
- `email: String`: Email address

#### UserSession
- `userId: String`: User ID
- `username: String`: Username
- `isActive: Boolean`: Session status
- `createdAt: Long`: Session creation timestamp

## Error Handling

The SDK provides comprehensive error handling through callback interfaces. Always implement the callback methods to handle potential errors:

```kotlin
alternaSDK.initialize(this, object : SDKCallback {
    override fun onError(error: String) {
        when {
            error.contains("network") -> {
                // Handle network errors
            }
            error.contains("session") -> {
                // Handle session errors
            }
            else -> {
                // Handle other errors
            }
        }
    }
})
```

## Best Practices

1. **Initialize Early**: Initialize the SDK in your Application class or main activity's onCreate
2. **Handle Callbacks**: Always implement callback methods to handle SDK events
3. **Session Management**: Check for active sessions before performing operations
4. **Error Handling**: Implement proper error handling for all SDK operations
5. **Testing**: Test the integration thoroughly in different scenarios

## Troubleshooting

### Common Issues

1. **SDK Not Initialized**: Make sure to call `initialize()` before using any SDK methods
2. **No Active Session**: Check if a user session exists before calling session-dependent methods
3. **Network Errors**: Ensure proper internet connectivity and network permissions
4. **ProGuard Issues**: The SDK includes consumer ProGuard rules, but ensure they're not overridden

### Debug Mode

Enable debug logging by adding the following to your Application class:

```kotlin
if (BuildConfig.DEBUG) {
    // Enable SDK debug logging
    Log.d("AlternaSDK", "Debug mode enabled")
}
```

## Support

For technical support and questions:
- Email: dev@alterna.com
- Documentation: [Link to documentation]
- Issues: [GitHub Issues Link]

## License

This SDK is licensed under the Apache License 2.0. See the LICENSE file for details.
