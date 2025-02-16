package com.synngate.twowaysync.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_servers")
data class RemoteServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String = ""
)