package com.example.hasta_kala.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@Composable
fun GhostKeypad(
    onKeyPress: (String) -> Unit
) {
    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "", "0", "BACK"
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 0 until 4) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                for (j in 0 until 3) {
                    val key = keys[i * 3 + j]
                    if (key.isNotEmpty()) {
                        KeyButton(
                            key = key,
                            onClick = { onKeyPress(key) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun KeyButton(
    key: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "keyBackgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        label = "keyContentColor"
    )

    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            ),
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (key == "BACK") {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    tint = contentColor
                )
            } else {
                Text(
                    text = key,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
            }
        }
    }
}
