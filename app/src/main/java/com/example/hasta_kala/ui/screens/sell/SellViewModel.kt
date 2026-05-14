package com.example.hasta_kala.ui.screens.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.db.entities.SaleItemEntity
import com.example.hasta_kala.data.repository.ProductRepository
import com.example.hasta_kala.data.repository.SaleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SellViewModel(
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _cart = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cart: StateFlow<Map<Int, CartItem>> = _cart

    val searchQuery = MutableStateFlow("")
    
    val categories: StateFlow<List<String>> = productRepository.allCategories
        .map { listOf("All Products") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All Products"))

    private val _selectedCategory = MutableStateFlow("All Products")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // Sale Details
    var customerName = MutableStateFlow("")
    var customerContact = MutableStateFlow("")
    var discountPercent = MutableStateFlow(0f)

    val sortBy = MutableStateFlow("A-Z")
    
    val products: StateFlow<List<ProductEntity>> = combine(
        _selectedCategory,
        searchQuery,
        sortBy,
        productRepository.allProducts
    ) { category, query, sort, allProds ->
        var filtered = allProds.filter {
            (category == "All Products" || it.category == category) &&
            (it.name.contains(query, ignoreCase = true) || it.sku.contains(query, ignoreCase = true))
        }
        
        when (sort) {
            "A-Z" -> filtered = filtered.sortedBy { it.name }
            "Z-A" -> filtered = filtered.sortedByDescending { it.name }
            "High Price" -> filtered = filtered.sortedByDescending { it.sellingPrice }
            "Low Price" -> filtered = filtered.sortedBy { it.sellingPrice }
            "High Popularity" -> filtered = filtered.sortedByDescending { it.stockQty } // Simulated
            "Low Popularity" -> filtered = filtered.sortedBy { it.stockQty }
            else -> filtered = filtered.sortedBy { it.name }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Totals
    val subtotal: StateFlow<Double> = _cart.map { cart ->
        cart.values.sumOf { it.product.sellingPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val tax: StateFlow<Double> = subtotal.map { it * 0.08 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val total: StateFlow<Double> = combine(subtotal, tax, discountPercent) { sub, tx, disc ->
        val subtotalWithTax = sub + tx
        val discountVal = subtotalWithTax * (disc / 100f)
        subtotalWithTax - discountVal
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[product.id] = CartItem(product, 1)
        }
        _cart.value = current
    }

    fun removeFromCart(productId: Int) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId]
        if (existing != null) {
            if (existing.quantity > 1) {
                current[productId] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.remove(productId)
            }
        }
        _cart.value = current
    }

    fun updateItemColor(productId: Int, color: String) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId]
        if (existing != null) {
            current[productId] = existing.copy(selectedColor = color)
            _cart.value = current
        }
    }

    fun updateItemSize(productId: Int, size: String) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId]
        if (existing != null) {
            current[productId] = existing.copy(selectedSize = size)
            _cart.value = current
        }
    }

    suspend fun saveSale(): Int? {
        val currentCart = _cart.value.values.toList()
        if (currentCart.isEmpty()) return null

        val sale = SaleEntity(
            billNumber = "HK-${System.currentTimeMillis() % 10000}",
            totalAmount = total.value,
            gstAmount = tax.value,
            discountAmount = subtotal.value * (discountPercent.value / 100.0),
            paymentMethod = "Cash",
            cashierName = customerName.value.ifBlank { "Guest" },
            customerContact = customerContact.value
        )
        
        val items = currentCart.map { cartItem ->
            SaleItemEntity(
                saleId = 0, // Assigned by DAO
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                quantity = cartItem.quantity,
                unitPrice = cartItem.product.sellingPrice,
                subtotal = cartItem.product.sellingPrice * cartItem.quantity,
                selectedColor = cartItem.selectedColor,
                selectedSize = cartItem.selectedSize
            )
        }
        
        return try {
            val sId = saleRepository.recordSale(sale, items)
            _cart.value = emptyMap() // Clear cart
            customerName.value = ""
            customerContact.value = ""
            discountPercent.value = 0f
            sId
        } catch (e: Exception) {
            null
        }
    }
}

data class CartItem(
    val product: ProductEntity,
    val quantity: Int,
    val selectedColor: String = "",
    val selectedSize: String = ""
)
