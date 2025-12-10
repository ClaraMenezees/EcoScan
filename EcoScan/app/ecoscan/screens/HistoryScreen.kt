package com.ifpe.ecoscan.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.ui.components.HistoryItem
import com.ifpe.ecoscan.viewmodel.HistoryEvent
import com.ifpe.ecoscan.viewmodel.HistoryUiState
import com.ifpe.ecoscan.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // garante carregamento inicial toda vez que abre a tela
    LaunchedEffect(Unit) { viewModel.loadHistory() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is HistoryEvent.ShowToast) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                is HistoryUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Carregando histórico...")
                    }
                }

                is HistoryUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📭 Histórico vazio", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Faça uma leitura para que os produtos apareçam aqui.")
                    }
                }

                is HistoryUiState.Error -> {
                    val message = (uiState as HistoryUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("❗ $message")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadHistory() }) {
                            Text("Tentar novamente")
                        }
                    }
                }

                is HistoryUiState.Success -> {
                    val items = (uiState as HistoryUiState.Success).items
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items) { product ->
                            HistoryItem(
                                product = product,
                                onClick = { onProductClick(product) },
                                onRemove = { viewModel.removeItem(product) }
                            )
                        }
                    }
                }
            }
        }
    }
}
