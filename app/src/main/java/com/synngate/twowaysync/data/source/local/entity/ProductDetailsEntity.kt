package com.synngate.twowaysync.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductDetailsEntity(
    @PrimaryKey val id: Int, // ID *ОСТАЕТСЯ* как Int (не nullable) и без значения по умолчанию
    val name: String,
    val barcode: String,
    val modified: Boolean = false // Признак модифицированности, по умолчанию false
)