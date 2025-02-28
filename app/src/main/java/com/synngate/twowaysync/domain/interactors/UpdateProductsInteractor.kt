package com.synngate.twowaysync.domain.interactors

interface UpdateProductsInteractor {

    suspend fun execute(): Result<Unit>
}