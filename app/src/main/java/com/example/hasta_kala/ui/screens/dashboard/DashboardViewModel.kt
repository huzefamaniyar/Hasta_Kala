package com.example.hasta_kala.ui.screens.dashboard

import com.example.hasta_kala.data.repository.SaleRepository
import com.example.hasta_kala.data.datastore.ShopPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

import com.example.hasta_kala.data.repository.ProductRepository
import java.text.SimpleDateFormat

class DashboardViewModel(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    private val shopPreferences: ShopPreferences
) : ViewModel() {

    private val _notifications = MutableSharedFlow<String>(replay = 0)
    val notifications = _notifications.asSharedFlow()

    val dashboardState: StateFlow<DashboardState> = combine(
        saleRepository.allSales,
        saleRepository.allSaleItems,
        productRepository.allProducts,
        shopPreferences.dailyTarget,
        shopPreferences.weeklyTarget,
        shopPreferences.monthlyTarget,
        shopPreferences.yearlyTarget
    ) { args: Array<Any> ->
        calculateDashboardState(
            sales = args[0] as List<com.example.hasta_kala.data.db.entities.SaleEntity>,
            items = args[1] as List<com.example.hasta_kala.data.db.entities.SaleItemEntity>,
            products = args[2] as List<com.example.hasta_kala.data.db.entities.ProductEntity>,
            dailyT = args[3] as Double,
            weeklyT = args[4] as Double,
            monthlyT = args[5] as Double,
            yearlyT = args[6] as Double
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    fun checkAndNotify(context: android.content.Context) {
        viewModelScope.launch {
            dashboardState.collect { state ->
                if (state.hasLowStock) {
                    state.lowStockItems.forEach { 
                        com.example.hasta_kala.util.NotificationHelper.showInventoryAlert(context, it.name, it.stockQty)
                    }
                }
                state.trendingProducts.forEach { 
                    com.example.hasta_kala.util.NotificationHelper.showTrendingAlert(context, it.name)
                }
            }
        }
    }

    private fun calculateDashboardState(
        sales: List<com.example.hasta_kala.data.db.entities.SaleEntity>,
        items: List<com.example.hasta_kala.data.db.entities.SaleItemEntity>,
        products: List<com.example.hasta_kala.data.db.entities.ProductEntity>,
        dailyT: Double,
        weeklyT: Double,
        monthlyT: Double,
        yearlyT: Double
    ): DashboardState {
        val now = Calendar.getInstance()
        
        // Revenue calculations
        val todaySales = sales.filter { isSameDay(it.createdAt, now) && it.status != "Refunded" }.sumOf { it.totalAmount }
        val weekSales = sales.filter { isSameWeek(it.createdAt, now) && it.status != "Refunded" }.sumOf { it.totalAmount }
        val monthSales = sales.filter { isSameMonth(it.createdAt, now) && it.status != "Refunded" }.sumOf { it.totalAmount }
        val yearSales = sales.filter { isSameYear(it.createdAt, now) && it.status != "Refunded" }.sumOf { it.totalAmount }

        // Best Sellers (Pie Chart)
        val bestSellersMap = items.groupBy { it.productId }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }
            .entries.sortedByDescending { it.value }
            .take(5)
        
        val pieEntries = bestSellersMap.map { entry ->
            val productName = products.find { it.id == entry.key }?.name ?: "Unknown"
            PieEntry(entry.value.toFloat(), productName)
        }

        // Trending Sales (Bar Chart - Last 7 Days)
        val last7DaysSales = (0..6).reversed().map { daysAgo ->
            val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
            val dayTotal = sales.filter { isSameDay(it.createdAt, date) && it.status != "Refunded" }.sumOf { it.totalAmount }
            val label = SimpleDateFormat("EEE", Locale.getDefault()).format(date.time)
            BarEntry(6f - daysAgo, dayTotal.toFloat()) to label
        }

        // Low Stock Detection
        val lowStockProducts = products.filter { it.stockQty <= it.lowStockThreshold }
        
        // Popular/Dead detection
        val deadInventory = products.filter { p -> items.none { it.productId == p.id } }
        val trendingProducts = bestSellersMap.take(1).mapNotNull { entry -> products.find { it.id == entry.key } }

        return DashboardState(
            dailyProgress = if (dailyT > 0) (todaySales / dailyT).toFloat() * 100f else 0f,
            weeklyProgress = if (weeklyT > 0) (weekSales / weeklyT).toFloat() * 100f else 0f,
            monthlyProgress = if (monthlyT > 0) (monthSales / monthlyT).toFloat() * 100f else 0f,
            yearlyProgress = if (yearlyT > 0) (yearSales / yearlyT).toFloat() * 100f else 0f,
            dailyRevenue = todaySales,
            weeklyRevenue = weekSales,
            monthlyRevenue = monthSales,
            yearlyRevenue = yearSales,
            bestSellers = pieEntries,
            trendingSales = last7DaysSales.map { it.first },
            trendingLabels = last7DaysSales.map { it.second },
            hasLowStock = lowStockProducts.isNotEmpty(),
            lowStockItems = lowStockProducts,
            deadInventory = deadInventory,
            trendingProducts = trendingProducts
        )
    }

    private fun isSameDay(time: Long, now: Calendar): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
    }

    private fun isSameWeek(time: Long, now: Calendar): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isSameMonth(time: Long, now: Calendar): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
    }

    private fun isSameYear(time: Long, now: Calendar): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
    }

    fun updateTarget(range: String, amount: String) {
        val targetValue = amount.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            shopPreferences.updateTarget(range, targetValue)
        }
    }
}

data class DashboardState(
    val dailyProgress: Float = 0f,
    val weeklyProgress: Float = 0f,
    val monthlyProgress: Float = 0f,
    val yearlyProgress: Float = 0f,
    val dailyRevenue: Double = 0.0,
    val weeklyRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val yearlyRevenue: Double = 0.0,
    val bestSellers: List<PieEntry> = emptyList(),
    val trendingSales: List<BarEntry> = emptyList(),
    val trendingLabels: List<String> = emptyList(),
    val hasLowStock: Boolean = false,
    val lowStockItems: List<com.example.hasta_kala.data.db.entities.ProductEntity> = emptyList(),
    val deadInventory: List<com.example.hasta_kala.data.db.entities.ProductEntity> = emptyList(),
    val trendingProducts: List<com.example.hasta_kala.data.db.entities.ProductEntity> = emptyList()
)
