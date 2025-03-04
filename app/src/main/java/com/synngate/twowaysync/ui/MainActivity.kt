package com.synngate.twowaysync.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.di.AppDependencies
import com.synngate.twowaysync.di.LogsScreensDpenedencies
import com.synngate.twowaysync.di.MainScreenDependencies
import com.synngate.twowaysync.di.ProductsScreensDependencies
import com.synngate.twowaysync.di.ServersGraphDependencies
import com.synngate.twowaysync.ui.screens.logs.LogsScreen
import com.synngate.twowaysync.ui.screens.logs.LogsScreenViewModel
import com.synngate.twowaysync.ui.screens.logs.LogsScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.main.MainScreen
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModel
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.products.ProductsScreen
import com.synngate.twowaysync.ui.screens.products.ProductsScreenViewModel
import com.synngate.twowaysync.ui.screens.products.ProductsScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.remoteserver.edit.ExternalServerScreen
import com.synngate.twowaysync.ui.screens.remoteserver.edit.ExternalServerScreenViewModel
import com.synngate.twowaysync.ui.screens.remoteserver.edit.ExternalServerScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.remoteserver.list.ExternalServersScreen
import com.synngate.twowaysync.ui.screens.remoteserver.list.ExternalServersScreenViewModel
import com.synngate.twowaysync.ui.screens.remoteserver.list.ExternalServersScreenViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.IO) {
            LogHelper.log("MainActivity created")
        }

        setContent {
            TwoWaySyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val appDependencies = (context.applicationContext as MyApplication).appDependencies

    NavHost(navController = navController, startDestination = "servers_graph") {

        navigation(startDestination = "servers_screen", route = "servers_graph") {
            serversGraph(appDependencies = appDependencies, navController = navController)
        }

        composable("main_screen") { backStackEntry ->
            val mainScreenDependencies = MainScreenDependencies(appDependencies)
            val factory = MainScreenViewModelFactory(mainScreenDependencies)
            val viewModel: MainScreenViewModel = viewModel(
                factory = factory,
                viewModelStoreOwner = backStackEntry
            )

            MainScreen(
                viewModel = viewModel,
                onCloseClicked = { navController.popBackStack() },
                onLogsClicked = { navController.navigate("logs_screen") },
                onProductsClicked = { navController.navigate("products_screen") }
            )
        }

        composable("logs_screen") { backStackEntry ->
            val logScreenDependencies = LogsScreensDpenedencies(appDependencies)
            val factory = LogsScreenViewModelFactory(logScreenDependencies)
            val viewModel: LogsScreenViewModel = viewModel(
                factory = factory,
                viewModelStoreOwner = backStackEntry
            )
            LogsScreen(
                viewModel = viewModel,
                onCloseClick = { navController.popBackStack() }
            )
        }

        composable("products_screen") { backStackEntry ->
            val productsScreenDependencies = ProductsScreensDependencies(appDependencies)
            val factory = ProductsScreenViewModelFactory(productsScreenDependencies)
            val viewModel: ProductsScreenViewModel = viewModel(
                factory = factory,
                viewModelStoreOwner = backStackEntry
            )
            ProductsScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

    }
}

fun NavGraphBuilder.serversGraph(
    appDependencies: AppDependencies,
    navController: NavHostController
) {

    composable("servers_screen") { backStackEntry ->
        val serversGraphDependencies = ServersGraphDependencies(appDependencies)
        var showExitDialog by rememberSaveable { mutableStateOf(false) }

        BackHandler(enabled = navController.currentBackStackEntry?.destination?.route == "servers_screen") {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            println("Current route: $currentRoute")
            if (currentRoute?.startsWith("servers_screen") == true || currentRoute == "servers_graph") {
                showExitDialog = true
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                serversGraphDependencies.clear()
            }
        }

        val activity = (LocalContext.current as Activity)

        val factory = ExternalServersScreenViewModelFactory(
            serversGraphDependencies.getExternalServersInteractor,
            dataStore = appDependencies.dataStore
        )
        val viewModel: ExternalServersScreenViewModel =
            viewModel(factory = factory, viewModelStoreOwner = backStackEntry)

        ExternalServersScreen(
            viewModel = viewModel,
            onContinueClick = { navController.navigate("main_screen") },
            onNewItemClick = { navController.navigate("server_screen/-1") },
            onServerItemClick = { navController.navigate("server_screen/$it") }
        )

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Выход из приложения") },
                text = { Text("Вы действительно хотите выйти?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        activity.finish()
                    }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Нет")
                    }
                }
            )
        }

    }

    composable(
        route = "server_screen/{serverId}",
        arguments = listOf(navArgument("serverId") { type = NavType.IntType })
    ) { backStackEntry ->
        val serversGraphDependencies = ServersGraphDependencies(appDependencies)

        DisposableEffect(Unit) {
            onDispose {
                serversGraphDependencies.clear()
            }
        }

        val factory = ExternalServerScreenViewModelFactory(
            serversGraphDependencies,
        )
        val serverId = backStackEntry.arguments?.getInt("serverId") ?: -1
        val viewModel: ExternalServerScreenViewModel = viewModel(
            factory = factory,
            viewModelStoreOwner = backStackEntry
        )
        ExternalServerScreen(
            viewModel = viewModel,
            serverId = serverId,
            onClickClose = { navController.popBackStack() },
            onClickDelete = { navController.popBackStack() }
        )
    }
}