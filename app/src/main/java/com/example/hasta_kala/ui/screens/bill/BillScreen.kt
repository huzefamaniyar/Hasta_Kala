package com.example.hasta_kala.ui.screens.bill

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hasta_kala.data.db.entities.SaleWithItems
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillScreen(
    saleId: Int,
    onBack: () -> Unit,
    viewModel: BillViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val saleWithItems by viewModel.saleWithItems.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Receipt", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = ManropeFontFamily)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFF95E14))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF1F0F09)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (saleWithItems != null) {
                ReceiptCard(saleWithItems!!)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        val sale = saleWithItems!!.sale
                        val items = saleWithItems!!.items
                        val shareText = buildString {
                            append("*Hasta-Kala Receipt*\n")
                            append("Bill No: #${sale.billNumber}\n")
                            append("Date: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(sale.createdAt))}\n\n")
                            items.forEach { append("${it.productName} x ${it.quantity} = ₹${it.subtotal}\n") }
                            append("\n*Total: ₹${sale.totalAmount}*")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            `package` = "com.whatsapp"
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(genericIntent, "Share Receipt"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF95E14))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Text("Share via WhatsApp", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { 
                        com.example.hasta_kala.util.PdfExportHelper.generateBillPdf(context, saleWithItems!!)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF95E14)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF95E14))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Text("Download PDF Receipt", style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                CircularProgressIndicator(color = Color(0xFFF95E14))
            }
        }
    }
}

@Composable
fun ReceiptCard(saleWithItems: SaleWithItems) {
    val sale = saleWithItems.sale
    val items = saleWithItems.items

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xFF453028))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(48.dp))
                Text(
                    "Hasta-Kala",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF95E14)
                    )
                )
                Text("Premium Artisan Goods", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                Text("Connaught Place, New Delhi", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
            }

            Spacer(modifier = Modifier.height(24.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Meta
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("DATE", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE3BFB2))
                    Text(
                        java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(sale.createdAt)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RECEIPT NO.", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE3BFB2))
                    Text("#${sale.billNumber}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Customer
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = androidx.compose.foundation.shape.CircleShape, color = Color(0xFF2D1B14), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(20.dp))
                    }
                }
                Column {
                    Text(sale.cashierName, style = MaterialTheme.typography.labelLarge, color = Color.White)
                    Text("Cashier", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Itemized List
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.productName, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text("Qty: ${item.quantity} × ₹${item.unitPrice}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE3BFB2))
                        }
                        Text("₹${item.subtotal}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Totals
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color(0xFFE3BFB2))
                    Text("₹${String.format("%.2f", sale.totalAmount - sale.gstAmount + sale.discountAmount)}", color = Color(0xFFE3BFB2))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (GST)", color = Color(0xFFE3BFB2))
                    Text("₹${String.format("%.2f", sale.gstAmount)}", color = Color(0xFFE3BFB2))
                }
                if (sale.discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount", color = Color(0xFFE3BFB2))
                        Text("-₹${String.format("%.2f", sale.discountAmount)}", color = Color.Red)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Paid", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text("₹${String.format("%.2f", sale.totalAmount)}", style = MaterialTheme.typography.headlineLarge.copy(fontFamily = ManropeFontFamily, color = Color(0xFFF95E14)))
                }
            }
        }
        
        // Zigzag Edge
        SerratedEdge(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun DashedDivider() {
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = Color(0xFFFDDBD0).copy(alpha = 0.15f),
            start = Offset(0f, 0.5f),
            end = Offset(size.width, 0.5f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Composable
fun SerratedEdge(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(20.dp)) {
        val toothWidth = 20.dp.toPx()
        val toothHeight = 15.dp.toPx()
        val path = Path()
        
        path.moveTo(0f, 0f)
        var x = 0f
        while (x < size.width) {
            path.lineTo(x + toothWidth / 2, toothHeight)
            path.lineTo(x + toothWidth, 0f)
            x += toothWidth
        }
        path.lineTo(size.width, 0f)
        path.close()
        
        drawPath(path, color = Color(0xFF1F0F09)) // Background color to "cut" the receipt
    }
}
