package com.synngate.twowaysync.data.source.local.dao

import androidx.room.*
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: ExternalServer): Long

    @Update
    suspend fun update(server: ExternalServer)

    @Delete
    suspend fun delete(server: ExternalServer)

    @Query("DELETE FROM external_servers WHERE id = :serverId")
    suspend fun delete(serverId: Int)

    @Query("SELECT * FROM external_servers WHERE id = :serverId")
    fun getServer(serverId: Int): Flow<ExternalServer?>

    @Query("SELECT * FROM external_servers")
    fun getAllServers(): Flow<List<ExternalServer>>
}