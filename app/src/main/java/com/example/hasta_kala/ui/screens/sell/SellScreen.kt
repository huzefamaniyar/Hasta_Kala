package com.example.hasta_kala.ui.screens.sell
import com.example.hasta_kala.navigation.NavRoutes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.components.CartSheet
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onSaleComplete: (Int) -> Unit,
    viewModel: SellViewModel
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val productList by viewModel.products.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val tax by viewModel.tax.collectAsState()
    val total by viewModel.total.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val customerContact by viewModel.customerContact.collectAsState()
    val discountPercent by viewModel.discountPercent.collectAsState()
    
    var showCartSheet by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    val sortBy by viewModel.sortBy.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        floatingActionButton = {
            if (cart.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showCartSheet = true },
                    containerColor = Color(0xFFF95E14),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    text = { Text("Checkout (${cart.size})") }
                )
            }
        },
        containerColor = Color(0xFF1F0F09)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search products...", color = Color(0xFFE3BFB2)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFF95E14)) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF95E14),
                        unfocusedBorderColor = Color(0xFF5A4138),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2D1B14),
                        unfocusedContainerColor = Color(0xFF2D1B14)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Sort Status Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { showSortDialog = true }
                        .background(Color(0xFF2D1B14).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Sort, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(18.dp))
                        Text("Sorted by: ", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                        Text(sortBy, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFE3BFB2))
                }

                // Category Filter
                val categories by viewModel.categories.collectAsState()
                LazyRow(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF95E14),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF2D1B14),
                                labelColor = Color(0xFFE3BFB2)
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                    }
                }

                // Product Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productList) { p ->
                        ProductCard(
                            product = p,
                            onClick = { viewModel.addToCart(p) },
                            onLongClick = { onNavigate(NavRoutes.ProductDetails.createRoute(p.id)) }
                        )
                    }
                }
            }


            // Sort Dialog
            if (showSortDialog) {
                AlertDialog(
                    onDismissRequest = { showSortDialog = false },
                    title = { Text("Sort Products By", color = Color.White) },
                    containerColor = Color(0xFF2D1B14),
                    text = {
                        Column {
                            listOf("A-Z", "Z-A", "High Price", "Low Price", "High Popularity", "Low Popularity").forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            viewModel.sortBy.value = option
                                            showSortDialog = false 
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(option, color = if (sortBy == option) Color(0xFFF95E14) else Color.White)
                                    if (sortBy == option) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF95E14))
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showSortDialog = false }) {
                            Text("CANCEL", color = Color(0xFFF95E14))
                        }
                    }
                )
            }

            // Cart Bottom Sheet
            if (showCartSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCartSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF2D1B14),
                    scrimColor = Color.Black.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(bottom = 32.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Checkout Summary", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            IconButton(onClick = { 
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) showCartSheet = false
                                }
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            }
                        }
                        
                        CartSheet(
                            cartItems = cart,
                            customerName = customerName,
                            onCustomerNameChange = { viewModel.customerName.value = it },
                            customerContact = customerContact,
                            onCustomerContactChange = { viewModel.customerContact.value = it },
                            discountPercent = discountPercent,
                            onDiscountChange = { viewModel.discountPercent.value = it },
                            onAddQuantity = { id: Int -> 
                                cart[id]?.product?.let { product -> viewModel.addToCart(product) } 
                            },
                            onRemoveQuantity = { id: Int -> viewModel.removeFromCart(id) },
                            onUpdateColor = { id: Int, color: String -> viewModel.updateItemColor(id, color) },
                            onUpdateSize = { id: Int, size: String -> viewModel.updateItemSize(id, size) },
                            subtotal = subtotal,
                            tax = tax,
                            total = total,
                            onSaveSale = {
                                scope.launch {
                                    viewModel.saveSale()?.let { newId ->
                                        sheetState.hide()
                                        showCartSheet = false
                                        onSaleComplete(newId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    product: ProductEntity, 
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column {
            Box(modifier = Modifier.aspectRatio(1f).background(Color(0xFF291710))) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Restored height
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Add Button (Top Right)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Color(0xFF1F0F09).copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape)
                        .clickable { onClick() }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFF95E14))
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy( // Restored style
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₹${product.sellingPrice}",
                    style = MaterialTheme.typography.titleLarge.copy( // Restored style
                        color = Color(0xFFF95E14),
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.stockQty} in stock",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (product.stockQty < 5) Color.Red else Color(0xFFE3BFB2),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SpecialActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, dashed: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        border = if (dashed) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF5A4138)) else null, // Real dashed border is harder in Compose, using solid for now
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFE3BFB2), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFFE3BFB2))
        }
    }
}
