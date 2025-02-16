package com.synngate.twowaysync.domain.model

data class RemoteServerDetails(
    val id: Int? = null,
    val name: String,
    val host: String,
    val port: Int,
    val username: String
)