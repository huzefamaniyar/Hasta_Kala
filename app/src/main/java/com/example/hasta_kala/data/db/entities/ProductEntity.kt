package com.example.hasta_kala.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sku: String = "",
    val name: String,
    val category: String,
    val mrp: Double,
    val sellingPrice: Double,
    val stockQty: Int,
    val lowStockThreshold: Int = 5,
    val imageUri: String = "",
    val colors: List<String> = emptyList(),
    val sizes: List<String> = emptyList()
)
