package com.synngate.twowaysync.ui.screens.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.synngate.twowaysync.domain.model.LogDetails
import com.synngate.twowaysync.ui.screens.main.MainScreenButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    factory: LogsScreenViewModelFactory,
    navController: NavController
) {

    val logScreenViewModel: LogsScreenViewModel = viewModel(factory = factory)
    val logs by logScreenViewModel.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Список логов") })
        },
        bottomBar = {
            MainScreenButton(
                text = "Закрыть",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                onClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                items(logs) { log ->
                    LogItem(log = log)
                }
            }
        }
    }
}

@Composable
fun LogItem(log: LogDetails, modifier: Modifier = Modifier) {
    Text(
        text = "${log.dateTime}: ${log.event}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(8.dp)
    )
}