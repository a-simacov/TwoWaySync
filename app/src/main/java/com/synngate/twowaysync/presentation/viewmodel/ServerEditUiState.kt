package com.synngate.twowaysync.presentation.viewmodel

import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity

sealed class ServerEditUiState {
    data object Initial : ServerEditUiState()
    data object Loading : ServerEditUiState()
    data class Loaded(
        val server: ExternalServerEntity,
        val isActive: Boolean,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : ServerEditUiState()
}
