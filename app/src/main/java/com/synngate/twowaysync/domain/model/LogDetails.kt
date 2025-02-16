package com.synngate.twowaysync.domain.model

import java.time.LocalDateTime

data class LogDetails(
    val id: Int? = null,
    val event: String,
    val level: String,
    val dateTime: LocalDateTime = LocalDateTime.now()
)