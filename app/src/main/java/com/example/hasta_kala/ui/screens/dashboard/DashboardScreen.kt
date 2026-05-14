package com.example.hasta_kala.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hasta_kala.ui.components.BestSellersChart
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.components.DashboardGauge
import com.example.hasta_kala.ui.components.TrendingBarChart
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onEditProduct: (Int) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.dashboardState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showTargetDialog by remember { mutableStateOf(false) }
    var targetAmount by remember { mutableStateOf("") }
    var selectedRange by remember { mutableStateOf("Daily") }
    var startDate by remember { mutableStateOf("Start Date") }
    var endDate by remember { mutableStateOf("End Date") }
    
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var pickingForStart by remember { mutableStateOf(true) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.checkAndNotify(context)
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        formatter.format(java.util.Date(it))
                    } ?: "Select Date"
                    if (pickingForStart) startDate = date else endDate = date
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTarget(selectedRange, targetAmount)
                        showTargetDialog = false
                        android.widget.Toast.makeText(context, "Target Set Successfully", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF95E14))
                ) {
                    Text("Set Target", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text("Cancel", color = Color(0xFFE3BFB2))
                }
            },
            title = {
                Text(
                    "Set Sales Target",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFDDBD0)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Range Selection
                    val ranges = listOf("Daily", "Weekly", "Monthly", "Yearly", "Custom")
                    var expanded by remember { mutableStateOf(false) }
                    
                    Box {
                        OutlinedTextField(
                            value = selectedRange,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Range", color = Color(0xFFE3BFB2)) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFFDDBD0))
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF95E14),
                                unfocusedBorderColor = Color(0xFF453028),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF2D1B14))
                        ) {
                            ranges.forEach { range ->
                                DropdownMenuItem(
                                    text = { Text(range, color = Color.White) },
                                    onClick = {
                                        selectedRange = range
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    if (selectedRange == "Custom") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { 
                                    pickingForStart = true
                                    showDatePicker = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFDDBD0)),
                                border = BoxShadowBorder()
                            ) {
                                Text(startDate, fontSize = 10.sp)
                            }
                            OutlinedButton(
                                onClick = { 
                                    pickingForStart = false
                                    showDatePicker = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFDDBD0)),
                                border = BoxShadowBorder()
                            ) {
                                Text(endDate, fontSize = 10.sp)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { targetAmount = it },
                        label = { Text("Target Amount (₹)", color = Color(0xFFE3BFB2)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF95E14),
                            unfocusedBorderColor = Color(0xFF453028),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            containerColor = Color(0xFF2D1B14),
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = Color(0xFF1F0F09)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Alert Banner
            if (state.hasLowStock) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF93000A).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFFB4AB).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFFFB4AB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Inventory is Low!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFFB4AB))
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Analytics",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDDBD0)
                        )
                    )
                    Text(
                        text = "Performance overview",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE3BFB2))
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = Color(0xFF39251E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { showTargetDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Adjust, contentDescription = "Target", tint = Color(0xFFFDDBD0))
                        }
                    }
                    Surface(
                        color = Color(0xFF39251E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                com.example.hasta_kala.util.PdfExportHelper.generateAnalyticsPdf(context, state)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Download, contentDescription = "Download", tint = Color(0xFFFDDBD0))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Gauges Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GaugeCard(label = "Daily", percentage = state.dailyProgress, modifier = Modifier.weight(1f))
                GaugeCard(label = "Weekly", percentage = state.weeklyProgress, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GaugeCard(label = "Monthly", percentage = state.monthlyProgress, modifier = Modifier.weight(1f))
                GaugeCard(label = "Yearly", percentage = state.yearlyProgress, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Best Sellers Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
                shape = RoundedCornerShape(24.dp),
                border = BoxShadowBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Best Sellers",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDDBD0)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BestSellersChart(
                        entries = state.bestSellers,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trending Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
                shape = RoundedCornerShape(24.dp),
                border = BoxShadowBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Trending",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDDBD0)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TrendingBarChart(
                        entries = state.trendingSales,
                        labels = state.trendingLabels,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Product Insights Section
            Text(
                text = "Product Insights",
                style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFF95E14), fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Low Stock
            if (state.lowStockItems.isNotEmpty()) {
                InsightCategory(title = "Low Stock (Update Needed)", items = state.lowStockItems, icon = Icons.Default.Inventory, color = Color(0xFFFFB4AB)) {
                    onEditProduct(it.id)
                }
            }

            // Popular / Trending
            if (state.trendingProducts.isNotEmpty()) {
                InsightCategory(title = "Popular (High Demand)", items = state.trendingProducts, icon = Icons.Default.TrendingUp, color = Color(0xFFC2E7FF)) {
                    onEditProduct(it.id)
                }
            }

            // Dead Inventory
            if (state.deadInventory.isNotEmpty()) {
                InsightCategory(title = "Dead Inventory (Action Recommended)", items = state.deadInventory, icon = Icons.Default.Block, color = Color(0xFFE3BFB2)) {
                    onEditProduct(it.id)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InsightCategory(
    title: String,
    items: List<com.example.hasta_kala.data.db.entities.ProductEntity>,
    icon: ImageVector,
    color: Color,
    onItemClick: (com.example.hasta_kala.data.db.entities.ProductEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        border = BoxShadowBorder(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelLarge, color = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            items.take(3).forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(product) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("SKU: ${product.sku} | Stock: ${product.stockQty}", color = Color(0xFFE3BFB2), fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF453028))
                }
            }
        }
    }
}


@Composable
fun GaugeCard(label: String, percentage: Float, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        border = BoxShadowBorder(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFE3BFB2))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                DashboardGauge(
                    percentage = percentage,
                    label = label,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun BoxShadowBorder() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = Color(0xFF453028).copy(alpha = 0.5f)
)
