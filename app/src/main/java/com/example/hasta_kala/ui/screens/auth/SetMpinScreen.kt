package com.example.hasta_kala.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hasta_kala.ui.components.GhostKeypad
import com.example.hasta_kala.ui.theme.ManropeFontFamily

enum class SetMpinStep { ENTER, CONFIRM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetMpinScreen(
    onMpinSet: (String) -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(SetMpinStep.ENTER) }
    var enteredMpin by remember { mutableStateOf("") }
    var confirmedMpin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val currentMpin = if (step == SetMpinStep.ENTER) enteredMpin else confirmedMpin

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set New MPIN", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
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
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (step == SetMpinStep.ENTER) "ENTER NEW 4-DIGIT MPIN" else "CONFIRM NEW 4-DIGIT MPIN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                MpinDots(pin = currentMpin)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Secondary state (disabled)
            if (step == SetMpinStep.ENTER) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = 0.5f }
                ) {
                    Text(
                        text = "CONFIRM NEW 4-DIGIT MPIN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MpinDots(pin = "")
                }
            } else {
                 Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = 0.5f }
                ) {
                    Text(
                        text = "ENTER NEW 4-DIGIT MPIN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MpinDots(pin = enteredMpin)
                }
            }

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            GhostKeypad(
                onKeyPress = { key ->
                    error = null
                    if (key == "BACK") {
                        if (step == SetMpinStep.ENTER) {
                            if (enteredMpin.isNotEmpty()) enteredMpin = enteredMpin.dropLast(1)
                        } else {
                            if (confirmedMpin.isNotEmpty()) confirmedMpin = confirmedMpin.dropLast(1)
                            else step = SetMpinStep.ENTER
                        }
                    } else {
                        if (step == SetMpinStep.ENTER) {
                            if (enteredMpin.length < 4) {
                                enteredMpin += key
                                if (enteredMpin.length == 4) step = SetMpinStep.CONFIRM
                            }
                        } else {
                            if (confirmedMpin.length < 4) {
                                confirmedMpin += key
                                if (confirmedMpin.length == 4) {
                                    if (enteredMpin == confirmedMpin) {
                                        onMpinSet(enteredMpin)
                                    } else {
                                        error = "MPINs do not match. Try again."
                                        confirmedMpin = ""
                                        step = SetMpinStep.ENTER
                                        enteredMpin = ""
                                    }
                                }
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { if (enteredMpin == confirmedMpin && enteredMpin.length == 4) onMpinSet(enteredMpin) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = enteredMpin == confirmedMpin && enteredMpin.length == 4,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF95E14), Color(0xFFE65100))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Text(
                            "Save MPIN",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MpinDots(pin: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        for (i in 0 until 4) {
            val filled = i < pin.length
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                    .border(
                        1.dp,
                        if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (filled) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}
