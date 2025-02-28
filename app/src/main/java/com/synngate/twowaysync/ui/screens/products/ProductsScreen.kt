package com.synngate.twowaysync.ui.screens.products

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synngate.twowaysync.domain.model.ProductDetails
import com.synngate.twowaysync.ui.screens.main.MainScreenButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsScreenViewModelInterface,
    onBackClicked: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val productsCount by viewModel.productsCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val serverStatus by viewModel.serverStatus.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Товары ($productsCount)") }) },
        bottomBar = {
            MainScreenButton(
                text = "Закрыть",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                onClick = { onBackClicked.invoke() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    ProductItem(product)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { viewModel.updateProducts() },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Update")
                }
                Button(
                    onClick = { viewModel.clearProducts() },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = serverStatus,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            message?.let {
                LaunchedEffect(it) {
                    delay(2000)
                    viewModel.clearMessage()
                }
                Text(
                    text = it,
                    color = if (it.contains("success", true)) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(product: ProductDetails, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${product.name} (id: ${product.id})",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = product.barcode,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ProductsScreenPreview() {
    // Фиктивные данные для превью
    val fakeProducts = listOf(
        ProductDetails(id = 1, name = "Продукт 1", barcode = "1234567890123"),
        ProductDetails(id = 2, name = "Продукт 2", barcode = "9876543210987")
    )
    val fakeSearchQuery = "Продукт"
    val fakeProductsCount = fakeProducts.size
    val fakeIsLoading = false
    val fakeMessage: String? = "Products updated successfully"
    val fakeServerStatus = "Server is available"

    // Создаём фейковый ViewModel или напрямую передаём состояния
    ProductsScreen(
        viewModel = object : ProductsScreenViewModelInterface {
            override val products: StateFlow<List<ProductDetails>> = MutableStateFlow(fakeProducts)
            override val searchQuery: StateFlow<String> = MutableStateFlow(fakeSearchQuery)
            override val productsCount: StateFlow<Int> = MutableStateFlow(fakeProductsCount)
            override val isLoading: StateFlow<Boolean> = MutableStateFlow(fakeIsLoading)
            override val message: StateFlow<String?> = MutableStateFlow(fakeMessage)
            override val serverStatus: StateFlow<String> = MutableStateFlow(fakeServerStatus)

            override fun onSearchQueryChanged(query: String) {}
            override fun updateProducts() {}
            override fun clearProducts() {}
            override fun clearMessage() {}
        },
        onBackClicked = {}
    )
}

// Интерфейс для фейкового ViewModel
interface ProductsScreenViewModelInterface {
    val products: StateFlow<List<ProductDetails>>
    val searchQuery: StateFlow<String>
    val productsCount: StateFlow<Int>
    val isLoading: StateFlow<Boolean>
    val message: StateFlow<String?>
    val serverStatus: StateFlow<String>
    fun onSearchQueryChanged(query: String)
    fun updateProducts()
    fun clearProducts()
    fun clearMessage()
}