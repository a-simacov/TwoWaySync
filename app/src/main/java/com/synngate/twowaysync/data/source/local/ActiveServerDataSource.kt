package com.synngate.twowaysync.data.source.local

import com.synngate.twowaysync.domain.model.ExternalServer

interface ActiveServerDataSource {

    suspend fun isActive(externalServer: ExternalServer): Boolean

    suspend fun update(externalServer: ExternalServer, isActive: Boolean)
}