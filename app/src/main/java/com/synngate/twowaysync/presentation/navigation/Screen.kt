package com.synngate.twowaysync.presentation.navigation

sealed class Screen(val route: String) {
    object ServersList : Screen("servers_list")
    object ServerEdit : Screen("server_edit/{serverId}") {
        fun createRoute(serverId: Int) = "server_edit/${serverId}"
    }
    object Main : Screen("main")
}
