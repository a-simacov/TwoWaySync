package com.synngate.twowaysync.domain.model

data class ProductFilter(
    val name: String? = null,
    val barcode: String? = null,
    val modified: Boolean? = null // Для фильтрации по признаку modified
)