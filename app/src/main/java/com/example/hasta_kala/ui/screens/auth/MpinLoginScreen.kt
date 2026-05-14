package com.example.hasta_kala.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hasta_kala.ui.components.GhostKeypad
import kotlinx.coroutines.delay

@Composable
fun MpinLoginScreen(
    shopName: String,
    onLoginSuccess: () -> Unit,
    onVerifyMpin: (String) -> Unit,
    isVerified: Boolean?,
    onResetVerification: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var mpin by remember { mutableStateOf("") }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isVerified) {
        if (isVerified == true) {
            onLoginSuccess()
        } else if (isVerified == false) {
            // Shake animation
            repeat(4) {
                shakeOffset.animateTo(10f, animationSpec = tween(50, easing = LinearEasing))
                shakeOffset.animateTo(-10f, animationSpec = tween(50, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f)
            delay(500)
            mpin = ""
            onResetVerification()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            modifier = Modifier
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = shopName,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter 4-Digit MPIN",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // PIN Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { translationX = shakeOffset.value }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                for (i in 0 until 4) {
                    val filled = i < mpin.length
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                if (filled) Color(0xFFE65100) else MaterialTheme.colorScheme.surfaceContainerHighest,
                                CircleShape
                            )
                            .then(
                                if (!filled) Modifier.border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                else Modifier
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TextButton(onClick = onForgotPassword) {
                Text(
                    "Forgot MPIN?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Keypad
        Box(modifier = Modifier.padding(bottom = 32.dp)) {
            GhostKeypad(
                onKeyPress = { key ->
                    if (key == "BACK") {
                        if (mpin.isNotEmpty()) mpin = mpin.dropLast(1)
                    } else {
                        if (mpin.length < 4) {
                            mpin += key
                            if (mpin.length == 4) {
                                onVerifyMpin(mpin)
                            }
                        }
                    }
                }
            )
        }
    }
}
