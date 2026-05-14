package com.example.hasta_kala.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_items",
    foreignKeys = [ForeignKey(
        entity = SaleEntity::class,
        parentColumns = ["id"],
        childColumns = ["saleId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val saleId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double,
    val selectedColor: String = "",
    val selectedSize: String = ""
)
