package com.synngate.twowaysync.data.repository

import com.synngate.twowaysync.domain.model.ExternalServer

interface ActiveServerRepository {

    suspend fun isActive(externalServer: ExternalServer): Boolean

    suspend fun update(externalServer: ExternalServer, isActive: Boolean)
}