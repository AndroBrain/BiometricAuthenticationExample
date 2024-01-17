package com.androbrain.biometricauthentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.androbrain.biometricauthentication.ui.theme.BiometricAuthenticationExampleTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authenticate()

        setContent {
            BiometricAuthenticationExampleTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = ::authenticate,
                    ) {
                        Text(text = "Authenticate")
                    }
                }
            }
        }
    }

    private fun authenticate() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Authenticate using biometrics
                val prompt = createBiometricPrompt()
                prompt.authenticate(createPromptInfo())
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // Biometric features hardware is missing
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Biometric features are currently unavailable.
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // The user didn't enroll in biometrics that your app accepts, prompt them to enroll in it
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent =
                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                    startActivityForResult(enrollIntent, 1333)
                }
            }
        }
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // If you've added negative button to prompt dialog
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Failure with unknown reason
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Authenticated successfully!
                Toast.makeText(
                    this@MainActivity,
                    "Authenticated with biometrics successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val executor = ContextCompat.getMainExecutor(this)
        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate with biometrics")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .setNegativeButtonText("Login with password")
            .build()
}
