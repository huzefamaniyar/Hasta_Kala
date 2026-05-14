package com.example.hasta_kala.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSetupScreen(
    onContinue: (String, String, String) -> Unit // shopName, ownerName, mobile (used for OTP)
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hasta-Kala Shop",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                "Welcome to Hasta-Kala",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "Set up your artisan profile to start managing your boutique.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SetupTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = "Shop Name",
                placeholder = "e.g. Golden Thread Weavers",
                icon = Icons.Default.Store
            )

            SetupTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = "Owner Name",
                placeholder = "e.g. Anaya Patel",
                icon = Icons.Default.Person
            )

            SetupTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = "Mobile Number",
                placeholder = "+91",
                icon = Icons.Default.PhoneIphone,
                keyboardType = KeyboardType.Phone
            )

            SetupTextField(
                value = gstNumber,
                onValueChange = { gstNumber = it },
                label = "GST Number",
                placeholder = "Optional",
                icon = Icons.Default.Description
            )

            SetupTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                placeholder = "Shop address",
                icon = Icons.Default.LocationOn
            )

            SetupTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category",
                placeholder = "e.g. Textiles, Pottery",
                icon = Icons.Default.Category
            )

            Spacer(modifier = Modifier.height(24.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            val isFormValid = shopName.isNotBlank() && ownerName.isNotBlank() && mobileNumber.length >= 10

            Button(
                onClick = { 
                    onContinue(shopName, ownerName, mobileNumber) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid,
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
                        Text(
                            "Continue",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SetupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.outlineVariant) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}
