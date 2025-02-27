package com.synngate.twowaysync.domain.common

import java.time.LocalDateTime

data class LogFilter(
    val event: String? = null,
    val level: String? = null,
    val dateTimeFrom: LocalDateTime? = null, //timestamp
    val dateTimeTo: LocalDateTime? = null //timestamp
)