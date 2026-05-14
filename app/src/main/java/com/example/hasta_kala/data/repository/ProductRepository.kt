package com.example.hasta_kala.data.repository

import com.example.hasta_kala.data.db.daos.ProductDao
import com.example.hasta_kala.data.db.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allCategories: Flow<List<String>> = productDao.getAllCategories()

    fun getByCategory(category: String): Flow<List<ProductEntity>> = 
        productDao.getByCategory(category)

    suspend fun getProductCount(): Int = productDao.getCount()

    suspend fun getProductById(id: Int): ProductEntity? = 
        productDao.getById(id)

    suspend fun addProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.delete(product)
    }

    suspend fun preSeedProducts() {
        val demoProducts = listOf(
            ProductEntity(
                sku = "HK-SAR-001",
                name = "Banarasi Silk Saree",
                mrp = 8500.0,
                sellingPrice = 7200.0,
                stockQty = 12,
                lowStockThreshold = 3,
                category = "Sarees",
                imageUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuBytWo93yNQOvvqRuJQ2t1oejd_jq4fMJhw23KQV9uAh7qQG_WoNnppUywQSE2NYXXn8wDIqZzztIkKPsyEEZ3lpcIcGdYL3AmnF5aaPyMMi2PPC9JkSTXLBxI9Xt5-qRqbQNj91bRJzUd8Z33s80uS9hH5Mqz-L0OrLp0_asCBH2CNWKcupF6lf1oTv-s3ILB37ayOA7JDTRPw2v_PFuLL8rTUVzxC1ygUvFZ4UCvIYNj5c_V9rgbsJPhLLFLtv4fHnAJpp8rdBLY",
                colors = listOf("Red", "Gold"),
                sizes = listOf("Standard")
            ),
            ProductEntity(
                sku = "HK-DUP-001",
                name = "Phulkari Dupatta",
                mrp = 2200.0,
                sellingPrice = 1850.0,
                stockQty = 4,
                lowStockThreshold = 5,
                category = "Dupattas",
                imageUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuDnPTxfDQq0muW3XNOK5ufAmAAVZCiMFl-IMcVku6uMtSe15tIEkxYuLA8l5i1T6HE9Rj9O4VoqQz048ZQleZaLIN0PVTnevx5Drjyu_cqCGcwJPEpINNoX-NSAqqbzYGuptpoVZcVeO4kf0DMADfpByJtoWwvquvDHHFAviMiE5ksZD3YvTbyAArCJ6pM35EPdsT4DP1Hv-OCdawxzWxumbNYBFNqheaoYE8KIv6OajdkV3J6XpaCB0j6cUR_W-CiB65unk868qnI",
                colors = listOf("Pink", "Yellow"),
                sizes = listOf("Free Size")
            ),
            ProductEntity(
                sku = "HK-KUR-001",
                name = "Chikankari Kurti",
                mrp = 3500.0,
                sellingPrice = 2800.0,
                stockQty = 2,
                lowStockThreshold = 3,
                category = "Kurtis",
                imageUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuBvvfk64F_DD3UTo4ueOXcM1rXGoGGOE3LjlxUBhKpj90yatW_N4hkRxjiwEsBLzNYt2t6yrAOOlkBeLUWncIs7C9cX23uYTLQ2_ju-T155KMwDSwrKhGEFcIFjER0yPgGjrw6PXERkVhHMXjLVN48SMfb-CVZ7vhPjwo18Jfq2Zdg0z9IA40f8vFNLCY8iEhIhS--yfW60Po2kM3rowcZLufC_dCND92HxkmLajsT1dLdK4hsj0cKF_7w57vVkhHTOqDoIOLMKmuE",
                colors = listOf("White", "Blue"),
                sizes = listOf("M", "L", "XL")
            ),
            ProductEntity(
                sku = "HK-ACC-001",
                name = "Terracotta Earrings",
                mrp = 850.0,
                sellingPrice = 650.0,
                stockQty = 25,
                lowStockThreshold = 5,
                category = "Accessories",
                imageUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuD9kQsFICqrdkciKZeACW7hi3-gKyfiyKgUSZhkBUV0LCKmbtedy5_vFy8AKB8ZWiJrj0joZ223w2nId8Lp-jxC_dS7Lo2lO9oOzo2LVSjo89am85i-jcWmsGEZ49ju7r__OBY6f77x2GTApF2qymDv-R6o_rvwJ6eFbfXVArZL2Cv6TLUz_6Jx8Ga4a0Ke7qHAuhdl0fsUjvMsccgS_9GNh2R66D9jV267j1fzrzbp3qFoUdunR4CkWnib6q3kGLSEN6J55KD59l4",
                colors = listOf("Brown", "Green"),
                sizes = listOf("Small")
            )
        )
        
        demoProducts.forEach { productDao.insert(it) }
    }
}
