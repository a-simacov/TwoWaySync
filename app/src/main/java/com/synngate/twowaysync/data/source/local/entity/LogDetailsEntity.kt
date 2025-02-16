package com.synngate.twowaysync.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "logs")
data class LogDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null, // Room будет генерировать id автоматически, если null
    val event: String,
    val level: String,
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime = LocalDateTime.now()
)