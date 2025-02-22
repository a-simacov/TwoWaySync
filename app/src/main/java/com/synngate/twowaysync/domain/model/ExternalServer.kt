package com.synngate.twowaysync.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "external_servers")
data class ExternalServer(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val host: String,
    val port: Int
)