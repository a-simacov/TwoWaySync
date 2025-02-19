package com.synngate.twowaysync.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "external_servers")
data class ExternalServerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val host: String,
    val port: Int
)
