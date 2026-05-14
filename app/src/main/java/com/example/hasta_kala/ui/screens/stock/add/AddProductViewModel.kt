package com.example.hasta_kala.ui.screens.stock.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product

    val existingCategories = productRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _product.value = productRepository.getProductById(productId)
        }
    }

    fun addProduct(
        name: String,
        mrp: Double,
        sellingPrice: Double,
        stockQty: Int,
        category: String,
        sku: String = "",
        imageUri: String = "",
        colors: List<String>,
        sizes: List<String>
    ) {
        viewModelScope.launch {
            val finalSku = if (sku.isBlank()) "HK-${category.take(3).uppercase()}-${System.currentTimeMillis().toString().takeLast(4)}" else sku
            val product = ProductEntity(
                sku = finalSku,
                name = name,
                mrp = mrp,
                sellingPrice = sellingPrice,
                stockQty = stockQty,
                lowStockThreshold = 5,
                category = category,
                imageUri = imageUri,
                colors = colors,
                sizes = sizes
            )
            productRepository.addProduct(product)
            _isSaved.value = true
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
            _isSaved.value = true
        }
    }
}
