package com.synngate.twowaysync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.synngate.twowaysync.presentation.ui.MainScreen
import com.synngate.twowaysync.presentation.ui.ServerEditScreen
import com.synngate.twowaysync.presentation.ui.ServersListScreen
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModel
import com.synngate.twowaysync.presentation.viewmodel.ServersListViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    serversListViewModel: ServersListViewModel,
    serverEditViewModel: ServerEditViewModel,
    modifier: Modifier = Modifier // Добавляем параметр
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ServersList.route,
        modifier = modifier
    ) {
        composable(Screen.ServersList.route) {
            ServersListScreen(
                onNavigate = { event -> navController.handleEvent(event) },
                viewModel = serversListViewModel,
                onServerClick = { serverId ->
                    navController.handleEvent(
                        NavigationEvent.NavigateTo(
                            Screen.ServerEdit.createRoute(
                                serverId
                            )
                        )
                    )
                },
                onAddServerClick = {
                    navController.handleEvent(
                        NavigationEvent.NavigateTo(
                            Screen.ServerEdit.createRoute(
                                -1
                            )
                        )
                    )
                },
                onContinueClick = {
                    navController.handleEvent(NavigationEvent.NavigateTo(Screen.Main.route))
                },
                onExitClick = { }
            )
        }
        composable(Screen.ServerEdit.route) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")?.toIntOrNull()

            ServerEditScreen(
                serverId = serverId,
                viewModel = serverEditViewModel,
                onSave = { },
                onDelete = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onSetActive = { serverId?.let { serverEditViewModel.setActiveServer() } }
            )
        }
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}
