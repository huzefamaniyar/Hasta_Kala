package com.example.hasta_kala.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.repository.SaleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val saleRepository: SaleRepository) : ViewModel() {

    val sortBy = MutableStateFlow("Newest")
    val selectedStatus = MutableStateFlow("All") // All, Paid, Refunded
    val searchQuery = MutableStateFlow("")

    val sales: StateFlow<List<SaleEntity>> = combine(
        saleRepository.allSales,
        sortBy,
        selectedStatus,
        searchQuery
    ) { allSales, sort, status, query ->
        var filteredList: List<SaleEntity> = allSales.filter {
            (it.billNumber.contains(query, ignoreCase = true) || it.cashierName.contains(query, ignoreCase = true)) &&
            (status == "All" || it.status == status)
        }
        
        when (sort) {
            "A-Z" -> filteredList.sortedBy { it.cashierName }
            "Z-A" -> filteredList.sortedByDescending { it.cashierName }
            "High Price" -> filteredList.sortedByDescending { it.totalAmount }
            "Low Price" -> filteredList.sortedBy { it.totalAmount }
            "High Popularity" -> filteredList.sortedByDescending { it.totalAmount }
            "Low Popularity" -> filteredList.sortedBy { it.totalAmount }
            else -> filteredList.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refundSale(saleId: Int, reason: String) {
        viewModelScope.launch {
            saleRepository.refundSale(saleId, reason)
        }
    }
}
