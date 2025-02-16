package com.synngate.twowaysync.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor

class MainScreenViewModelFactory(
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(getMainScreenDataInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}