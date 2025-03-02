package com.synngate.twowaysync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetails(
    val id: Int,
    val name: String,
    val barcode: String,
    val modified: Boolean = false
)