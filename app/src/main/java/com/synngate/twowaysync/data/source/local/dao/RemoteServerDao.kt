package com.synngate.twowaysync.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.synngate.twowaysync.data.source.local.entity.RemoteServerEntity

@Dao
interface RemoteServerDao {
    @Query("SELECT * FROM remote_servers")
    suspend fun getAll(): List<RemoteServerEntity>

    @Insert
    suspend fun insert(server: RemoteServerEntity)

    // Можно добавить другие методы DAO при необходимости (например, для удаления, обновления, поиска сервера по ID)
}