package com.synngate.twowaysync.data.repository.impl

import com.synngate.twowaysync.data.repository.ActiveServerRepository
import com.synngate.twowaysync.data.source.local.ActiveServerDataSource
import com.synngate.twowaysync.domain.model.ExternalServer

class ActiveServerRepositoryImpl(
    private val dataSource: ActiveServerDataSource
) : ActiveServerRepository {

    override suspend fun isActive(externalServer: ExternalServer): Boolean {
        return dataSource.isActive(externalServer)
    }

    override suspend fun update(externalServer: ExternalServer, isActive: Boolean) {
        dataSource.update(externalServer, isActive)
    }
}