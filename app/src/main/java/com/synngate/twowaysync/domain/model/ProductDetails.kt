package com.synngate.twowaysync.domain.model

data class ProductDetails(
    val id: Int, // ID теперь Int (не nullable) - соответствует ProductDetailsEntity
    val name: String,
    val barcode: String,
    val modified: Boolean = false
)