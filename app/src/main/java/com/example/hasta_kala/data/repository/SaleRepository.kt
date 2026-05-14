package com.example.hasta_kala.data.repository

import androidx.room.withTransaction
import com.example.hasta_kala.data.db.AppDatabase
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.db.entities.SaleItemEntity
import com.example.hasta_kala.data.db.entities.SaleWithItems
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val db: AppDatabase) {
    private val saleDao = db.saleDao()
    private val productDao = db.productDao()

    val allSales: Flow<List<SaleEntity>> = saleDao.getAllSales()
    val allSaleItems: Flow<List<SaleItemEntity>> = saleDao.getAllSaleItems()

    fun getSaleWithItems(saleId: Int): Flow<SaleWithItems> = 
        saleDao.getSaleWithItems(saleId)

    suspend fun refundSale(saleId: Int, reason: String) {
        saleDao.refundSale(saleId, reason)
    }

    suspend fun recordSale(sale: SaleEntity, items: List<SaleItemEntity>): Int {
        return db.withTransaction {
            // Record sale and items
            val saleId = saleDao.insertSale(sale).toInt()
            val itemsWithSaleId = items.map { it.copy(saleId = saleId) }
            saleDao.insertSaleItems(itemsWithSaleId)
            
            // Decrement stock for each product
            items.forEach { item ->
                val product = productDao.getById(item.productId)
                if (product != null) {
                    productDao.update(product.copy(stockQty = (product.stockQty - item.quantity).coerceAtLeast(0)))
                }
            }
            
            saleId
        }
    }
}
