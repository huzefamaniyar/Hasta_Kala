package com.example.hasta_kala.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hasta_kala.data.db.daos.ProductDao
import com.example.hasta_kala.data.db.daos.SaleDao
import com.example.hasta_kala.data.db.entities.ProductEntity
import com.example.hasta_kala.data.db.entities.SaleEntity
import com.example.hasta_kala.data.db.entities.SaleItemEntity
import androidx.room.TypeConverters

@Database(
    entities = [ProductEntity::class, SaleEntity::class, SaleItemEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hasta_kala_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
