package com.synngate.twowaysync.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.synngate.twowaysync.MyApp
import com.synngate.twowaysync.data.repository.ServerRepository
import com.synngate.twowaysync.presentation.navigation.LocalNavController
import com.synngate.twowaysync.presentation.navigation.NavigationGraph
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModel
import com.synngate.twowaysync.presentation.viewmodel.ServerEditViewModelFactory
import com.synngate.twowaysync.presentation.viewmodel.ServersListViewModel
import com.synngate.twowaysync.presentation.viewmodel.ServersListViewModelFactory
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TwoWaySyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val navController = rememberNavController()

                        CompositionLocalProvider(LocalNavController provides navController) {
                            NavigationGraph(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}