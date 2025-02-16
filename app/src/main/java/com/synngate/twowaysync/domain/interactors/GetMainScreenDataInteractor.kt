package com.synngate.twowaysync.domain.interactors

import com.synngate.twowaysync.data.common.Result
import com.synngate.twowaysync.domain.model.MainScreenData

interface GetMainScreenDataInteractor {
    suspend fun invoke(): Result<MainScreenData>
}