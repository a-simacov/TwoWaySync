package com.synngate.twowaysync.presentation.navigation

sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    object Empty : NavigationEvent()
}
