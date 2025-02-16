package com.synngate.twowaysync.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_servers")
data class RemoteServerDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val host: String,
    val port: Int,
    val username: String = "",
    val password: String = "",
    val authEndpoint: String = "",
    val echoEndpoint: String = "",
)