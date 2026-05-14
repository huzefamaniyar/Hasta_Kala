package com.example.hasta_kala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hasta_kala.ui.screens.sell.CartItem
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@Composable
fun CartSheet(
    cartItems: Map<Int, CartItem>,
    customerName: String,
    onCustomerNameChange: (String) -> Unit,
    customerContact: String,
    onCustomerContactChange: (String) -> Unit,
    discountPercent: Float,
    onDiscountChange: (Float) -> Unit,
    onAddQuantity: (Int) -> Unit,
    onRemoveQuantity: (Int) -> Unit,
    onUpdateColor: (Int, String) -> Unit,
    onUpdateSize: (Int, String) -> Unit,
    subtotal: Double,
    tax: Double,
    total: Double,
    onSaveSale: () -> Unit
) {
    val isFormValid = customerName.isNotBlank() && customerContact.isNotBlank()
    val colors = listOf("#1E3A8A", "#78350F", "#065F46", "#991B1B")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f) // Allow more space for the list
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        // Items List
        Text(
            "Cart Items (${cartItems.size})",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems.values.toList()) { item ->
                CartItemRow(
                    item = item,
                    colors = colors,
                    onAdd = { onAddQuantity(item.product.id) },
                    onRemove = { onRemoveQuantity(item.product.id) },
                    onColorSelect = { onUpdateColor(item.product.id, it) },
                    onSizeSelect = { onUpdateSize(item.product.id, it) }
                )
            }
        }

        Divider(color = Color(0xFF5A4138).copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))

        // Billing Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Billing Details",
                style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFFF95E14), fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = onCustomerNameChange,
                    label = { Text("Name", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF95E14),
                        unfocusedBorderColor = Color(0xFF5A4138),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = customerContact,
                    onValueChange = onCustomerContactChange,
                    label = { Text("Contact", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF95E14),
                        unfocusedBorderColor = Color(0xFF5A4138),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = if (discountPercent == 0f) "" else discountPercent.toString(),
                onValueChange = { onDiscountChange(it.toFloatOrNull() ?: 0f) },
                label = { Text("Discount %", fontSize = 12.sp) },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                shape = RoundedCornerShape(12.dp),
                suffix = { Text("%", color = Color(0xFFE3BFB2)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF95E14),
                    unfocusedBorderColor = Color(0xFF5A4138),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        Surface(
            color = Color(0xFF39251E).copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Amount", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("₹${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFF95E14), fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveSale,
            enabled = isFormValid && cartItems.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF95E14), disabledContainerColor = Color(0xFF39251E)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    if (isFormValid && cartItems.isNotEmpty()) Brush.verticalGradient(colors = listOf(Color(0xFFF95E14), Color(0xFFD04B0C)))
                    else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFormValid && cartItems.isNotEmpty()) "Finalize Transaction" else "Complete Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    colors: List<String>,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onColorSelect: (String) -> Unit,
    onSizeSelect: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF453028).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.imageUri,
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp), maxLines = 1)
                Text("₹${item.product.sellingPrice}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFF95E14)))
                
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    colors.forEach { colorHex ->
                        val isSelected = item.selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .border(width = if (isSelected) 1.dp else 0.dp, color = Color.White, shape = CircleShape)
                                .clickable { onColorSelect(colorHex) }
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 6.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.product.sizes.forEach { size ->
                        val isSelected = item.selectedSize == size
                        Surface(
                            modifier = Modifier.clickable { onSizeSelect(size) },
                            color = if (isSelected) Color(0xFFF95E14) else Color(0xFF2D1B14),
                            shape = RoundedCornerShape(4.dp),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF5A4138))
                        ) {
                            Text(
                                size,
                                color = if (isSelected) Color.White else Color(0xFFE3BFB2),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Increased spacing
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp).background(Color(0xFF2D1B14), CircleShape)) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
                Box(contentAlignment = Alignment.Center, modifier = Modifier.widthIn(min = 24.dp)) { // Fixed width for number
                    Text(item.quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                IconButton(onClick = onAdd, modifier = Modifier.size(24.dp).background(Color(0xFF2D1B14), CircleShape)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}
