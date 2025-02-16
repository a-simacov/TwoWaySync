package com.synngate.twowaysync.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels // <---- ИМПОРТ: viewModels() для создания ViewModel
import androidx.compose.material3.Scaffold
import androidx.lifecycle.lifecycleScope
import com.synngate.twowaysync.MyApplication
import com.synngate.twowaysync.domain.interactors.GetMainScreenDataInteractor
import com.synngate.twowaysync.domain.model.MainScreenData
import com.synngate.twowaysync.ui.theme.TwoWaySyncTheme
import com.synngate.twowaysync.util.LogHelper
import kotlinx.coroutines.launch

private val MainScreenVerticalPadding = 16.dp
private val MainScreenTitleBottomPadding = 24.dp
private val MainScreenButtonVerticalSpacing = 16.dp
private val MainScreenStatusBottomPadding = 24.dp

class MainActivity : ComponentActivity() {

    // Получаем зависимость GetMainScreenDataInteractor через AppDependencies
    private val getMainScreenDataInteractor: GetMainScreenDataInteractor
        get() = MyApplication.appDependencies.provideGetMainScreenDataInteractor()

    // Создаем MainScreenViewModel с помощью viewModels()
    private val mainScreenViewModel: MainScreenViewModel by viewModels {
        MainScreenViewModelFactory(getMainScreenDataInteractor) // <---- Используем MainScreenViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                        MainScreenContent( // <---- ИЗМЕНЕНИЕ: Передаем ViewModel в MainScreenContent
                            name = "Android",
                            modifier = Modifier.padding(innerPadding),
                            mainScreenViewModel = mainScreenViewModel // <---- Передаем ViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RectangleShape
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun MainScreenContent(name: String, modifier: Modifier = Modifier, mainScreenViewModel: MainScreenViewModel) { // <---- ИЗМЕНЕНИЕ: Принимаем MainScreenViewModel
    // Получаем StateFlow с данными из ViewModel и преобразуем его в State для Compose
    val mainScreenDataState: MainScreenData by mainScreenViewModel.mainScreenDataState.collectAsState() // <---- ИЗМЕНЕНИЕ: Собираем StateFlow из ViewModel

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MainScreenVerticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Главный экран",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = MainScreenTitleBottomPadding)
        )

        // Вертикальный список кнопок
        Column(
            verticalArrangement = Arrangement.spacedBy(MainScreenButtonVerticalSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            MainScreenButton(
                text = "Логи: ${mainScreenDataState.logCount}", // <---- ИЗМЕНЕНИЕ: Используем non-nullable mainScreenDataState.logCount напрямую
                onClick = { /* TODO: Обработка нажатия кнопки "Логи" */ }
            )
            MainScreenButton(
                text = "Серверы: ${mainScreenDataState.remoteServerCount}", // <---- ИЗМЕНЕНИЕ: Используем non-nullable mainScreenDataState.remoteServerCount напрямую
                onClick = { /* TODO: Обработка нажатия кнопки "Серверы" */ }
            )
            MainScreenButton(
                text = "Товары: ${mainScreenDataState.productCount}", // <---- ИЗМЕНЕНИЕ: Используем non-nullable mainScreenDataState.productCount напрямую
                onClick = { /* TODO: Обработка нажатия кнопки "Товары" */ }
            )
        }

        // Горизонтальная группа статусов внизу экрана
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
                Text(text = mainScreenDataState.remoteServerStatus) // <---- ИЗМЕНЕНИЕ: Используем non-nullable mainScreenDataState.remoteServerStatus напрямую
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Локальный веб-сервер:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = mainScreenDataState.localWebServerStatus) // <---- ИЗМЕНЕНИЕ: Используем non-nullable mainScreenDataState.localWebServerStatus напрямую
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    TwoWaySyncTheme {
        // MainScreenContent("Android", getMainScreenDataInteractor = MyApplication.appDependencies.getMainScreenDataInteractor) // <---- УДАЛЕНО: Preview больше не работает так напрямую
        Text("Main Screen Preview не реализован с ViewModel") // <---- Временное сообщение для Preview
    }
}