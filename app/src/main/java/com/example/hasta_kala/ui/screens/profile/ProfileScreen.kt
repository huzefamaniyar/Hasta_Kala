package com.example.hasta_kala.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val shopName by viewModel.shopName.collectAsState(initial = "Hasta-Kala")
    val ownerName by viewModel.ownerName.collectAsState(initial = "")
    val mobile by viewModel.mobileNumber.collectAsState(initial = "")
    val profileImageUri by viewModel.profileImageUri.collectAsState(initial = null)
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var showEditDetails by remember { mutableStateOf(false) }
    
    var tempShopName by remember(shopName) { mutableStateOf(shopName) }
    var tempOwnerName by remember(ownerName) { mutableStateOf(ownerName) }
    var tempMobile by remember(mobile) { mutableStateOf(mobile) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.updateProfileImage(uri.toString())
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = Color(0xFF1F0F09)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color(0xFF2D1B14)
                    ) {
                        if (profileImageUri != null) {
                            coil.compose.AsyncImage(
                                model = profileImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    ownerName.take(1).ifBlank { "S" },
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color(0xFFF95E14),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .background(Color(0xFFF95E14), CircleShape)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        shopName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        ownerName.ifBlank { "Store Owner" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE3BFB2)
                    )
                    if (profileImageUri != null) {
                        Text(
                            "Remove Image",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEF5350),
                            modifier = Modifier.padding(top = 4.dp).clickable { viewModel.updateProfileImage(null) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bento Grid (Simplified)
            Text("Business Management", style = MaterialTheme.typography.labelLarge, color = Color(0xFFF95E14))
            Spacer(modifier = Modifier.height(12.dp))
            
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Edit Business Details",
                subtitle = "Manage shop name, owner, and contact",
                icon = Icons.Default.EditNote,
                onClick = { showEditDetails = true }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Security Settings", style = MaterialTheme.typography.labelLarge, color = Color(0xFFF95E14))
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingsItem(
                icon = Icons.Default.LockReset, 
                title = "Security & MPIN", 
                description = "Secure your app with a new MPIN",
                onClick = { 
                    val randomOtp = (100000..999999).random().toString()
                    com.example.hasta_kala.util.NotificationHelper.showOtpNotification(context, randomOtp)
                    // We need a way to pass this OTP to AppNavGraph. 
                    // For now, I'll simulate navigation to Verify route.
                    onNavigate("security_verify/$randomOtp")
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { onNavigate("login") }, // Simple logout for now
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF93000A).copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93000A).copy(alpha = 0.5f))
            ) {
                Text("Logout from Device", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Edit Details Dialog
        if (showEditDetails) {
            AlertDialog(
                onDismissRequest = { showEditDetails = false },
                title = { Text("Edit Business Info", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = tempShopName,
                            onValueChange = { tempShopName = it },
                            label = { Text("Shop Name") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = tempOwnerName,
                            onValueChange = { tempOwnerName = it },
                            label = { Text("Owner Name") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = tempMobile,
                            onValueChange = { tempMobile = it },
                            label = { Text("Mobile Number") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateShopDetails(tempShopName, tempOwnerName, tempMobile)
                        showEditDetails = false
                    }) { Text("Save Changes", color = Color(0xFFF95E14)) }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDetails = false }) { Text("Cancel", color = Color.White) }
                },
                containerColor = Color(0xFF2D1B14)
            )
        }
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = Color(0xFF2D1B14),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFF39251E)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color(0xFFE3BFB2), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2D1B14)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFFDDBD0), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(description, color = Color(0xFFE3BFB2), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF5A4138))
        }
    }
}
