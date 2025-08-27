# SDK

A dummy Android SDK that implements the complete user flow for onboarding, user details display, withdrawal processing, and WPS (Wallet Payment System) integration.

## Overview

This SDK implements the flow described in the attached diagram:

1. **SDK Handling**
   - Step 1: Onboard/Login - Create a session for user
   - Display user details (salary, withdrawal limits, eligibility, transaction history)
   - Withdrawal page with limits and consent popups
   - Callback exposure to WPS for their side of logic

2. **WPS Handling**
   - User validation (password/PIN)
   - Eligibility check
   - Success screen with receipt and transaction status
   - Return to homepage option

## Project Structure

```
android-sdk/
├── sdk/                           # SDK library module
│   ├── src/main/java/com/alterna/sdk/
│   │   ├── AlternaSDK.kt         # Main SDK class
│   │   ├── callbacks/            # Callback interfaces
│   │   ├── models/               # Data models
│   │   ├── ui/                   # UI activities
│   │   └── wps/                  # WPS integration
│   └── src/main/res/             # SDK resources
├── app/                           # Test application
│   └── src/main/java/com/alterna/sdk/app/
└── build.gradle                   # Root build configuration
```

## Key Components

### Core SDK Classes

- **`SDK`**: Main singleton class that manages the SDK flow
- **`WPSHandler`**: Handles WPS validation and eligibility checks
- **`User`**: Data model for user information
- **`UserSession`**: Manages active user sessions
- **`Transaction`**: Represents transaction data

### Callback Interfaces

- **`SDKCallback`**: Main SDK events (initialization, login, errors)
- **`WithdrawalCallback`**: Withdrawal-specific callbacks for WPS integration

### UI Activities

- **`OnboardingActivity`**: Login and session creation
- **`UserDetailsActivity`**: Display user information and limits
- **`WithdrawalActivity`**: Withdrawal form with consent popups
- **`SuccessActivity`**: Success screen with receipt

## Usage

### 1. Initialize the SDK

```kotlin
val sdk = SDK.getInstance()
sdk.initialize(context, object : SDKCallback {
    override fun onSDKInitialized() {
        // SDK ready to use
    }
    
    override fun onError(message: String) {
        // Handle errors
    }
    
    // ... other callback methods
})
```

### 2. Start User Flow

```kotlin
// Start onboarding/login
sdk.startOnboarding(context)

// Show user details (requires active session)
sdk.showUserDetails(context)

// Show withdrawal page
sdk.showWithdrawalPage(context, withdrawalCallback)
```

### 3. Handle Withdrawal Callbacks

```kotlin
val withdrawalCallback = object : WithdrawalCallback {
    override fun onWithdrawalRequested(amount: Double, callback: (Boolean, String?) -> Unit) {
        // This callback is exposed to WPS for their side of logic
        // WPS validates user and processes withdrawal
        callback(true, null) // Success
    }
    
    override fun onWithdrawalCompleted(transactionId: String, amount: Double) {
        // Withdrawal completed
    }
}
```

## WPS Integration

The SDK exposes callbacks to WPS for:

1. **User Validation**: Password/PIN verification
2. **Eligibility Check**: Verify user can make withdrawals
3. **Transaction Processing**: Handle the actual withdrawal
4. **Completion Notification**: Inform SDK of transaction status

## Flow Implementation

### Step 1: Onboarding/Login
- User enters credentials
- SDK creates user session
- Navigates to user details

### User Details Display
- Shows salary and withdrawal limits
- Performs eligibility check
- Displays transaction history
- Provides exit SDK option

### Withdrawal Process
- User enters withdrawal amount
- Shows consent and privacy policy popups
- Exposes callback to WPS
- WPS validates user and processes withdrawal

### Success Flow
- Shows transaction receipt
- Informs user of transaction status
- Option to return to homepage

## Building and Testing

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+
- Gradle 8.0+

### Build Commands

```bash
# Build the entire project
./gradlew build

# Build only the SDK
./gradlew :sdk:assembleRelease

# Build the test app
./gradlew :app:assembleDebug
```

### Running the Test App

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the `app` module on an emulator or device
4. Use the test app to explore SDK functionality

## Customization

The SDK is designed to be easily customizable:

- **Themes**: Modify colors and styles in `res/values/themes.xml`
- **UI**: Customize layouts in `res/layout/` directory
- **Business Logic**: Extend models and handlers for your specific needs
- **WPS Integration**: Implement your own WPS validation logic

## Dependencies

- **AndroidX Core**: Core Android functionality
- **Material Design**: Modern UI components
- **ConstraintLayout**: Flexible layouts
- **Navigation**: Activity navigation
- **Lifecycle**: Activity lifecycle management

## License

This is a dummy SDK for demonstration purposes. Modify as needed for your specific use case.

## Support

For questions or issues with this dummy SDK implementation, please refer to the code comments and documentation within the source files.
