package com.synngate.twowaysync.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.ui.screens.main.MainScreen
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModel
import com.synngate.twowaysync.ui.screens.main.MainScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.remoteserver.edit.ExternalServerScreen
import com.synngate.twowaysync.ui.screens.remoteserver.edit.ExternalServerScreenViewModelFactory
import com.synngate.twowaysync.ui.screens.remoteserver.list.ExternalServersScreen
import com.synngate.twowaysync.ui.screens.remoteserver.list.ExternalServersScreenViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val remoteServerConnectionManager = MyApplication.appDependencies.provideRemoteServerConnectionManager()
//        lifecycleScope.launch { // <----  Запускаем корутину для suspend функций
//            remoteServerConnectionManager.setCurrentServerId(123) // <----  Сохраняем ID = 123
//            val currentServerId = remoteServerConnectionManager.getCurrentServerId() // <----  Читаем ID
//            Log.d("MainActivity", "Current Server ID from DataStore: $currentServerId") // <----  Логируем результат
//
//            remoteServerConnectionManager.setCurrentServerId(null) // <----  Сохраняем ID = null (отключаем сервер)
//            val currentServerIdNull = remoteServerConnectionManager.getCurrentServerId() // <----  Читаем ID снова
//            Log.d("MainActivity", "Current Server ID (after setting null): $currentServerIdNull") // <---- Логируем результат
//        }

        // Программная запись лога при создании MainActivity
        lifecycleScope.launch {
            LogHelper.log("MainActivity created") // <----  Используем LogHelper для записи лога
        }
        Log.d("MainActivity", "onCreate called")

        setContent {
            TwoWaySyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        TwoWaySyncApp()
                    }
                }
            }
        }
    }
}

@Composable
fun TwoWaySyncApp() {
    val context = LocalContext.current

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "servers_screen") {
        composable("servers_screen") {
            val factory = ExternalServersScreenViewModelFactory(
                context = context,
                navController = navController
            )

            ExternalServersScreen(factory, navController)
        }
        composable(
            route = "server_screen/{serverId}",
            arguments = listOf(navArgument("serverId") { type = NavType.IntType })
        ) { backStackEntry ->
            val factory = ExternalServerScreenViewModelFactory(
                context = context,
                navController = navController
            )
            val serverId = backStackEntry.arguments?.getInt("serverId") ?: -1

            ExternalServerScreen(
                factory = factory,
                navController = navController,
                serverId = serverId
            )
        }
        composable("main_screen") {
            val getMainScreenDataInteractor =
                MyApplication.appDependencies.provideGetMainScreenDataInteractor()
            val factory = MainScreenViewModelFactory(getMainScreenDataInteractor)

            MainScreen(factory = factory, navController = navController)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    TwoWaySyncTheme {
        TwoWaySyncApp()
    }
}