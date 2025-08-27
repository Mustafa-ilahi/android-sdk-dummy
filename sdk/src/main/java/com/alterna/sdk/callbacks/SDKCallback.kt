package com.alterna.sdk.callbacks

/**
 * Main callback interface for SDK events
 */
interface SDKCallback {
    /**
     * Called when SDK initialization is complete
     */
    fun onSDKInitialized()
    
    /**
     * Called when an error occurs
     */
    fun onError(message: String)
    
    /**
     * Called when user successfully logs in
     */
    fun onUserLoggedIn(userId: String)
    
    /**
     * Called when user logs out
     */
    fun onUserLoggedOut()
    
    /**
     * Called when SDK is exited
     */
    fun onSDKExited()
}
