package com.synngate.twowaysync.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationHandler(
    navController: NavHostController,
    navigationEvents: StateFlow<NavigationEvent>
) {
    val currentEvent by navigationEvents.collectAsState()

    LaunchedEffect(currentEvent) {
        currentEvent?.let { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> navController.navigate(event.route)
                is NavigationEvent.NavigateBack -> navController.popBackStack()
                NavigationEvent.Empty -> {} // Просто очищаем событие
            }
            navController.handleEvent(NavigationEvent.Empty) // Очистка события
        }
    }
}

fun NavHostController.handleEvent(event: NavigationEvent) {
    when (event) {
        is NavigationEvent.NavigateTo -> navigate(event.route)
        is NavigationEvent.NavigateBack -> popBackStack()
        NavigationEvent.Empty -> {} // Событие сброса
    }
}