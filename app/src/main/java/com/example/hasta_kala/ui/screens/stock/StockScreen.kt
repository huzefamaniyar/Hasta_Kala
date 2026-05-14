package com.example.hasta_kala.ui.screens.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Int) -> Unit,
    viewModel: StockViewModel
) {
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedStockLevel by viewModel.selectedStockLevel.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product?", color = Color.White) },
            text = { Text("Are you sure you want to remove ${productToDelete?.name}? This action cannot be undone.", color = Color(0xFFE3BFB2)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(productToDelete!!.id)
                    productToDelete = null
                }) { Text("Delete", color = Color(0xFFEF5350)) }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Cancel", color = Color.White) }
            },
            containerColor = Color(0xFF2D1B14)
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = Color(0xFFF95E14),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = Color(0xFF1F0F09)
    ) { padding ->
        val categories by viewModel.categories.collectAsState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Text(
                "Manage Inventory",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            // Search Bar
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search SKU or Name", color = Color(0xFFE3BFB2), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF95E14),
                        unfocusedBorderColor = Color(0xFF5A4138),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D1B14),
                        unfocusedContainerColor = Color(0xFF2D1B14)
                    )
                )
                IconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier.background(Color(0xFF2D1B14), RoundedCornerShape(12.dp)).size(52.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color(0xFFF95E14))
                }
            }

            // Category Chips
            LazyRow(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedCategory.value = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFF95E14),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2D1B14),
                            labelColor = Color(0xFFE3BFB2)
                        ),
                        border = null
                    )
                }
            }

            // Inventory List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    InventoryItemCard(
                        product = product,
                        onEdit = { onEditProduct(product.id) },
                        onDelete = { productToDelete = product }
                    )
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xFF2D1B14)
            ) {
                Column(modifier = Modifier.padding(20.dp).padding(bottom = 48.dp)) {
                    Text("Filters & Sorting", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Inventory Health", style = MaterialTheme.typography.labelLarge, color = Color(0xFFF95E14))
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StockLevelChip("All", Color.Gray, selectedStockLevel == "All") { viewModel.selectedStockLevel.value = "All" }
                        StockLevelChip("Full", Color(0xFF81C784), selectedStockLevel == "Green") { viewModel.selectedStockLevel.value = "Green" }
                        StockLevelChip("Medium", Color(0xFFFFB74D), selectedStockLevel == "Yellow") { viewModel.selectedStockLevel.value = "Yellow" }
                        StockLevelChip("Low", Color(0xFFEF5350), selectedStockLevel == "Red") { viewModel.selectedStockLevel.value = "Red" }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Sort By", style = MaterialTheme.typography.labelLarge, color = Color(0xFFF95E14))
                    val sortOptions = listOf("Name (A-Z)", "Name (Z-A)", "Stock (High-Low)", "Stock (Low-High)", "Price (High-Low)", "Price (Low-High)")
                    val sortBy by viewModel.sortBy.collectAsState()
                    
                    Row(
                        modifier = Modifier.padding(top = 12.dp).fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sortOptions.forEach { option ->
                            FilterChip(
                                selected = sortBy == option,
                                onClick = { viewModel.sortBy.value = option },
                                label = { Text(option, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF95E14),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFF39251E),
                                    labelColor = Color(0xFFE3BFB2)
                                ),
                                border = null
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF95E14))
                    ) {
                        Text("Apply Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StockLevelChip(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) color else Color(0xFF39251E),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = if (selected) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InventoryItemCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val stockColor = when {
        product.stockQty <= product.lowStockThreshold -> Color(0xFFEF5350) // Red
        product.stockQty <= product.lowStockThreshold * 2 -> Color(0xFFFFB74D) // Yellow
        else -> Color(0xFF81C784) // Green
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text("SKU: ${product.sku}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                Text(product.category, style = MaterialTheme.typography.labelSmall, color = Color(0xFFF95E14))
                
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).background(Color(0xFF39251E), CircleShape)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).background(Color(0xFF39251E), CircleShape)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${product.stockQty}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = stockColor,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("units", style = MaterialTheme.typography.labelSmall, color = stockColor)
            }
        }
    }
}
