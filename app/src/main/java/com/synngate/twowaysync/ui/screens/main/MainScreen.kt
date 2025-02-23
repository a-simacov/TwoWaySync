package com.synngate.twowaysync.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.synngate.twowaysync.domain.model.MainScreenData

private val MainScreenVerticalPadding = 16.dp
private val MainScreenTitleBottomPadding = 24.dp
private val MainScreenButtonVerticalSpacing = 16.dp
private val MainScreenStatusBottomPadding = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    factory: MainScreenViewModelFactory,
    navController: NavController
) {

    val mainScreenViewModel: MainScreenViewModel = viewModel(factory = factory)
    val mainScreenDataState: MainScreenData by mainScreenViewModel.mainScreenDataState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Главный экран") })
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
            Column(
                verticalArrangement = Arrangement.spacedBy(MainScreenButtonVerticalSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                MainScreenButton(
                    text = "Логи: ${mainScreenDataState.logCount}",
                    onClick = { /* TODO: Обработка нажатия кнопки "Логи" */ }
                )
                MainScreenButton(
                    text = "Серверы: ${mainScreenDataState.remoteServerCount}",
                    onClick = { /* TODO: Обработка нажатия кнопки "Серверы" */ }
                )
                MainScreenButton(
                    text = "Товары: ${mainScreenDataState.productCount}",
                    onClick = { /* TODO: Обработка нажатия кнопки "Товары" */ }
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MainScreenStatusBottomPadding)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Удаленный сервер:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = mainScreenDataState.remoteServerStatus)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Локальный веб-сервер:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = mainScreenDataState.localWebServerStatus)
                }
            }
        }
    }
}

@Composable
fun MainScreenButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RectangleShape,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
