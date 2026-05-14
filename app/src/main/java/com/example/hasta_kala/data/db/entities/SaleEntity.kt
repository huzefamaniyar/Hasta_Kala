package com.example.hasta_kala.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val billNumber: String,
    val totalAmount: Double,
    val gstAmount: Double,
    val discountAmount: Double,
    val paymentMethod: String,
    val status: String = "Paid",
    val cashierName: String = "",
    val customerContact: String = "",
    val refundReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
