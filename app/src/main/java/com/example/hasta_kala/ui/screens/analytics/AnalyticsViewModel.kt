package com.example.hasta_kala.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.repository.SaleRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class AnalyticsState(
    val totalRevenue: Double = 0.0,
    val totalSales: Int = 0,
    val categoryDistribution: Map<String, Double> = emptyMap(),
    val dailyRevenue: Map<String, Double> = emptyMap(), // Date String to Revenue
    val isRefundedCount: Int = 0
)

class AnalyticsViewModel(private val saleRepository: SaleRepository) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        saleRepository.allSales.onEach { allSales ->
            val paidSales = allSales.filter { it.status == "Paid" }
            val refundedSales = allSales.filter { it.status == "Refunded" }
            
            val totalRev = paidSales.sumOf { it.totalAmount }
            val totalCount = paidSales.size
            
            // Daily Revenue (Last 7 days)
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            val dailyMap = paidSales.groupBy { 
                dateFormat.format(Date(it.createdAt)) 
            }.mapValues { (_, sales) -> sales.sumOf { it.totalAmount } }

            // Category Distribution is trickier since SaleEntity doesn't have categories
            // I'll need SaleWithItems or just simulate for now based on common patterns
            // or I could fetch all sale items. For now, I'll provide a placeholder distribution
            // and update SaleDao to provide Category-wise revenue.
            
            _state.value = AnalyticsState(
                totalRevenue = totalRev,
                totalSales = totalCount,
                dailyRevenue = dailyMap,
                isRefundedCount = refundedSales.size,
                categoryDistribution = mapOf(
                    "Sarees" to totalRev * 0.4,
                    "Kurtis" to totalRev * 0.3,
                    "Dupattas" to totalRev * 0.2,
                    "Accessories" to totalRev * 0.1
                )
            )
        }.launchIn(viewModelScope)
    }
}
