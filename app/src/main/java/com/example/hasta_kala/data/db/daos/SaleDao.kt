package com.example.hasta_kala.data.db.daos

import androidx.room.*
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.db.entities.SaleItemEntity
import com.example.hasta_kala.data.db.entities.SaleWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sale_items")
    fun getAllSaleItems(): Flow<List<SaleItemEntity>>

    @Query("SELECT * FROM sales WHERE createdAt BETWEEN :from AND :to ORDER BY createdAt DESC")
    fun getSalesBetween(from: Long, to: Long): Flow<List<SaleEntity>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE status = 'Paid' AND createdAt BETWEEN :from AND :to")
    fun getTotalRevenue(from: Long, to: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales WHERE createdAt BETWEEN :from AND :to")
    fun getSaleCount(from: Long, to: Long): Flow<Int>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId")
    fun getSaleWithItems(saleId: Int): Flow<SaleWithItems>

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("UPDATE sales SET status = 'Refunded', refundReason = :reason WHERE id = :saleId")
    suspend fun refundSale(saleId: Int, reason: String)

    @Transaction
    suspend fun recordSale(sale: SaleEntity, items: List<SaleItemEntity>): Int {
        val saleId = insertSale(sale).toInt()
        val itemsWithSaleId = items.map { it.copy(saleId = saleId) }
        insertSaleItems(itemsWithSaleId)
        return saleId
    }
}
