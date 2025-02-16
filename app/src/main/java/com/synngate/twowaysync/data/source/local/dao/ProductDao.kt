package com.synngate.twowaysync.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.synngate.twowaysync.data.source.local.entity.ProductDetailsEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductDetailsEntity>

    @Insert
    suspend fun insert(product: ProductDetailsEntity)

    // Можно добавить другие методы DAO при необходимости (например, для удаления, обновления, поиска товара по ID или штрихкоду)
}