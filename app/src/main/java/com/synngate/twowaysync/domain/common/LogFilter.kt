package com.synngate.twowaysync.domain.common

data class LogFilter(
    val event: String? = null,
    val level: String? = null,
    val dateTimeFrom: Long? = null, //timestamp
    val dateTimeTo: Long? = null //timestamp
)