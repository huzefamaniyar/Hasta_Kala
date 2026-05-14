package com.example.hasta_kala.ui.screens.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class StockViewModel(private val productRepository: ProductRepository) : ViewModel() {

    val selectedCategory = MutableStateFlow("All")
    val selectedStockLevel = MutableStateFlow("All") // Green, Yellow, Red, All
    val searchQuery = MutableStateFlow("")
    
    val categories: StateFlow<List<String>> = productRepository.allCategories
        .map { listOf("All") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    val sortBy = MutableStateFlow("Name (A-Z)")

    val products: StateFlow<List<ProductEntity>> = combine(
        productRepository.allProducts,
        selectedCategory,
        selectedStockLevel,
        searchQuery,
        sortBy
    ) { allProds, category, level, query, sort ->
        var filtered = allProds.filter { prod ->
            val matchesCategory = category == "All" || prod.category == category
            val matchesQuery = prod.name.contains(query, ignoreCase = true) || prod.sku.contains(query, ignoreCase = true)
            val matchesLevel = when (level) {
                "Green" -> prod.stockQty > prod.lowStockThreshold * 2
                "Yellow" -> prod.stockQty in (prod.lowStockThreshold + 1)..(prod.lowStockThreshold * 2)
                "Red" -> prod.stockQty <= prod.lowStockThreshold
                else -> true
            }
            matchesCategory && matchesQuery && matchesLevel
        }
        
        when (sort) {
            "Name (A-Z)" -> filtered.sortedBy { it.name }
            "Name (Z-A)" -> filtered.sortedByDescending { it.name }
            "Stock (High-Low)" -> filtered.sortedByDescending { it.stockQty }
            "Stock (Low-High)" -> filtered.sortedBy { it.stockQty }
            "Price (High-Low)" -> filtered.sortedByDescending { it.sellingPrice }
            "Price (Low-High)" -> filtered.sortedBy { it.sellingPrice }
            else -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            val product = productRepository.allProducts.first().find { it.id == productId }
            if (product != null) {
                productRepository.deleteProduct(product)
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }
}
