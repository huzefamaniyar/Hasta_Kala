package com.example.hasta_kala.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.data.repository.ProductRepository
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productId: Int,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product

    init {
        viewModelScope.launch {
            _product.value = productRepository.getProductById(productId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Int,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel
) {
    val product by viewModel.product.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF1F0F09)
    ) { innerPadding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF95E14))
            }
        } else {
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                // Header Image
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    AsyncImage(
                        model = p.imageUri,
                        contentDescription = p.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF39251E))
                    )
                    
                    // Back Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = p.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFF95E14)
                    )
                    
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${p.sellingPrice}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFFF95E14),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            color = if (p.stockQty > 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (p.stockQty > 0) "In Stock (${p.stockQty})" else "Out of Stock",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF5A4138).copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))

                    DetailRow("SKU", p.sku)
                    DetailRow("Category", p.category)

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Stats / Details
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailRow("Low Stock Alert", p.lowStockThreshold.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFE3BFB2), style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text(text = "$name Screen (Coming Soon)", color = androidx.compose.ui.graphics.Color.White)
    }
}
