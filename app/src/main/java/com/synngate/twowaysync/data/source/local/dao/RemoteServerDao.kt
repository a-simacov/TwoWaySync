package com.synngate.twowaysync.data.source.local.dao

import androidx.room.*
import com.synngate.twowaysync.domain.model.RemoteServerDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // <---- Аннотация для insert
    suspend fun insert(server: RemoteServerDetails)

    @Update // <---- Аннотация для update
    suspend fun update(server: RemoteServerDetails)

    @Delete
    suspend fun delete(server: RemoteServerDetails) // <---- Метод delete, принимающий RemoteServerDetails (как есть)

    @Query("DELETE FROM remote_servers WHERE id = :serverId") // <---- НОВЫЙ метод delete, принимающий serverId
    suspend fun delete(serverId: Int)

    @Query("SELECT * FROM remote_servers WHERE id = :serverId") // <---- Аннотация и запрос для getServer
    suspend fun getServer(serverId: Int): RemoteServerDetails?

    @Query("SELECT * FROM remote_servers") // <---- Аннотация и запрос для getAllServers
    fun getAllServers(): Flow<List<RemoteServerDetails>>

    // @Insert(onConflict = OnConflictStrategy.REPLACE) // <----  Аннотация для saveRemoteServer - если был ранее, удалите или закомментируйте
    // suspend fun saveRemoteServer(server: RemoteServerDetails) // <----  Метод saveRemoteServer - если был ранее, удалите или закомментируйте

}