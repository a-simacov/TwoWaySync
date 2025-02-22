package com.synngate.twowaysync.domain.interactors.impl

import com.synngate.twowaysync.domain.interactors.impl.network.RetrofitClient
import com.synngate.twowaysync.domain.interactors.CheckServerAvailabilityInteractor
import com.synngate.twowaysync.domain.model.ExternalServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CheckServerAvailabilityInteractorImpl : CheckServerAvailabilityInteractor {

    override fun execute(serverDetails: ExternalServer): Flow<Boolean> = flow {
        val baseUrl = "https://${serverDetails.host}:${serverDetails.port}"
        val apiService = RetrofitClient.getApiService(baseUrl)

        try {
            val response = apiService.echo()
            emit(response.isSuccessful)
        } catch (e: IOException) {
            emit(false)
            e.printStackTrace()
        }
    }
}