package com.example.hasta_kala.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    expectedOtp: String,
    onVerify: () -> Unit,
    onBack: () -> Unit,
    onResend: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            } else {
                delay(1000L)
            }
        }
    }

    // Auto-verify on 6th digit
    LaunchedEffect(otp) {
        if (otp.length == 6) {
            if (otp == expectedOtp) {
                onVerify()
            } else {
                error = "Invalid OTP. Please check your notifications."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Mobile", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Enter OTP",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
            )
            
            Text(
                "A 6-digit code has been sent to your registered mobile number.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp).fillMaxWidth()
            )

            OtpDisplay(otp = otp, isError = error != null)

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                TextButton(
                    onClick = { 
                        if (timeLeft == 0) {
                            timeLeft = 30
                            otp = ""
                            error = null
                            onResend()
                        }
                    },
                    enabled = timeLeft == 0
                ) {
                    Text(
                        if (timeLeft > 0) "Resend in ${timeLeft}s" else "Resend OTP",
                        color = if (timeLeft > 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Didn't receive the code?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            com.example.hasta_kala.ui.components.GhostKeypad(
                onKeyPress = { key ->
                    error = null
                    if (key == "BACK") {
                        if (otp.isNotEmpty()) otp = otp.dropLast(1)
                    } else if (key == "DONE") { // The keypad has a DONE/Enter button
                         if (otp.length == 6) {
                            if (otp == expectedOtp) onVerify()
                            else error = "Invalid OTP. Please check your notifications."
                        }
                    } else {
                        if (otp.length < 6) {
                            otp += key
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OtpDisplay(otp: String, isError: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 6) {
            val char = otp.getOrNull(i)?.toString() ?: ""
            val isFocused = otp.length == i
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .background(
                        if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceContainer, 
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        2.dp,
                        when {
                            isError -> MaterialTheme.colorScheme.error
                            isFocused -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
