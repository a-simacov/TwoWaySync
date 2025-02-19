package com.synngate.twowaysync.data.local.db.dao

import androidx.room.*
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalServersDao {
    @Query("SELECT * FROM external_servers")
    fun getAll(): Flow<List<ExternalServerEntity>>

    @Query("SELECT * FROM external_servers WHERE id = :id")
    fun getById(id: Int): Flow<ExternalServerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: ExternalServerEntity): Long

    @Update
    suspend fun update(server: ExternalServerEntity)

    @Delete
    suspend fun delete(server: ExternalServerEntity)

    @Query("DELETE FROM external_servers WHERE id = :serverId")
    suspend fun deleteById(serverId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(server: ExternalServerEntity): Long

}
