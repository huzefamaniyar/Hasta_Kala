package com.example.hasta_kala.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onViewBill: (Int) -> Unit,
    viewModel: HistoryViewModel
) {
    val sales by viewModel.sales.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var showSortDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedSaleForRefund by remember { mutableStateOf<SaleEntity?>(null) }
    var refundReason by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = Color(0xFF1F0F09)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Sales History",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Row {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Color(0xFFF95E14))
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color(0xFFF95E14))
                    }
                }
            }

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search by Bill # or Customer", color = Color(0xFFE3BFB2)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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

            // Sales List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sales) { sale ->
                    SaleHistoryCard(
                        sale = sale, 
                        onClick = { onViewBill(sale.id) },
                        onRefund = { selectedSaleForRefund = sale }
                    )
                }
            }
        }

        // Refund Dialog
        if (selectedSaleForRefund != null) {
            AlertDialog(
                onDismissRequest = { 
                    selectedSaleForRefund = null
                    refundReason = ""
                },
                containerColor = Color(0xFF2D1B14),
                title = { Text("Confirm Refund", color = Color.White) },
                text = {
                    Column {
                        Text("Are you sure you want to refund this sale (${selectedSaleForRefund?.billNumber})?", color = Color(0xFFE3BFB2), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = refundReason,
                            onValueChange = { refundReason = it },
                            label = { Text("Reason for Refund") },
                            placeholder = { Text("Enter refund reason...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF95E14),
                                unfocusedBorderColor = Color(0xFF5A4138),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (refundReason.isNotBlank()) {
                                viewModel.refundSale(selectedSaleForRefund!!.id, refundReason)
                                selectedSaleForRefund = null
                                refundReason = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                        enabled = refundReason.isNotBlank()
                    ) {
                        Text("Confirm Refund", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        selectedSaleForRefund = null 
                        refundReason = ""
                    }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }

        // Status Filter Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xFF2D1B14)
            ) {
                Column(modifier = Modifier.padding(20.dp).padding(bottom = 32.dp)) {
                    Text("Filter by Status", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    listOf("All", "Paid", "Refunded").forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.selectedStatus.value = status
                                    showFilterSheet = false 
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(status, color = if (selectedStatus == status) Color(0xFFF95E14) else Color.White)
                            if (selectedStatus == status) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF95E14))
                        }
                    }
                }
            }
        }

        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = { Text("Sort Sales", color = Color.White) },
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
                        Text("Close", color = Color(0xFFF95E14))
                    }
                }
            )
        }
    }
}

@Composable
fun SaleHistoryCard(sale: SaleEntity, onClick: () -> Unit, onRefund: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(sale.createdAt))
    val isRefunded = sale.status == "Refunded"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(sale.billNumber, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        if (isRefunded) {
                            Surface(color = Color(0xFFEF5350).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("REFUNDED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color(0xFFEF5350), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                    Text("Customer: ${sale.cashierName}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                    Text(dateStr, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "₹${String.format("%.2f", sale.totalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isRefunded) Color.Gray else Color(0xFFF95E14),
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = if (isRefunded) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    Text(sale.paymentMethod, style = MaterialTheme.typography.labelSmall, color = if (isRefunded) Color.Gray else Color(0xFF81C784))
                }
            }
            
            if (!isRefunded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF5A4138).copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onRefund,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF5350))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Issue Refund", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else if (sale.refundReason != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason: ${sale.refundReason}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFEF5350).copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
