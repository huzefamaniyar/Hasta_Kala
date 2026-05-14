package com.example.hasta_kala.ui.screens.analytics

import android.view.ViewGroup
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.hasta_kala.ui.components.BottomNavBar
import com.example.hasta_kala.ui.theme.ManropeFontFamily
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: AnalyticsViewModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = Color(0xFF1F0F09)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                "Business Insights",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Revenue", "₹${String.format("%.0f", state.totalRevenue)}", Icons.Default.Payments, Color(0xFF81C784), Modifier.weight(1f))
                StatCard("Total Sales", "${state.totalSales}", Icons.Default.ShoppingBag, Color(0xFF64B5F6), Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Revenue Trend Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Revenue Trend", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    RevenueBarChart(state.dailyRevenue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Distribution Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Category-wise Distribution", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryPieChart(state.categoryDistribution)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Refund Alert Card
            if (state.isRefundedCount > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFEF5350))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "You have ${state.isRefundedCount} refunded transactions in your history.",
                            color = Color(0xFFEF5350),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B14)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, color = Color(0xFFE3BFB2), fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun CategoryPieChart(data: Map<String, Double>) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = android.graphics.Color.WHITE
                setHoleColor(android.graphics.Color.TRANSPARENT)
                setCenterTextColor(android.graphics.Color.WHITE)
                animateY(1000)
            }
        },
        update = { chart ->
            val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
            val dataSet = PieDataSet(entries, "Categories").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 12f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun RevenueBarChart(data: Map<String, Double>) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.textColor = android.graphics.Color.WHITE
                axisLeft.textColor = android.graphics.Color.WHITE
                axisRight.isEnabled = false
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                animateY(1000)
            }
        },
        update = { chart ->
            val entries = data.values.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }
            val labels = data.keys.toList()
            
            val dataSet = BarDataSet(entries, "Revenue").apply {
                color = android.graphics.Color.parseColor("#F95E14")
                valueTextColor = android.graphics.Color.WHITE
            }
            
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}
