package com.example.hasta_kala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hasta_kala.navigation.NavRoutes
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("Home", NavRoutes.Dashboard.route, "dashboard"),
        NavigationItem("Sell", NavRoutes.Sell.route, "point_of_sale"),
        NavigationItem("Stock", NavRoutes.Stock.route, "inventory_2"),
        NavigationItem("Sales", NavRoutes.History.route, "receipt_long"),
        NavigationItem("More", NavRoutes.Settings.route, "settings")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF2D1B14))
            .shadow(12.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFF4E342E).copy(alpha = 0.4f) else Color.Transparent)
                    .clickable { onNavigate(item.route) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = getIconForName(item.iconName),
                    contentDescription = item.label,
                    tint = if (isSelected) Color(0xFFE65100) else Color(0xFFF5F5F5).copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.sp,
                        color = if (isSelected) Color(0xFFE65100) else Color(0xFFF5F5F5).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val route: String,
    val iconName: String
)

@Composable
fun getIconForName(name: String): ImageVector {
    return when (name) {
        "dashboard" -> androidx.compose.material.icons.Icons.Default.Dashboard
        "point_of_sale" -> androidx.compose.material.icons.Icons.Default.PointOfSale
        "inventory_2" -> androidx.compose.material.icons.Icons.Default.Inventory2
        "receipt_long" -> androidx.compose.material.icons.Icons.Default.ReceiptLong
        "settings" -> androidx.compose.material.icons.Icons.Default.Settings
        else -> androidx.compose.material.icons.Icons.Default.Home
    }
}
