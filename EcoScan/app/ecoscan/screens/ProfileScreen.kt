package com.ifpe.ecoscan.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifpe.ecoscan.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Box
            }

            state.user?.let { user ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            tonalElevation = 4.dp,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = user.name?.firstOrNull()?.toString() ?: "U")
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = user.name ?: "Usuário",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = user.email ?: "-",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Status: ${
                                    if (user.verified == true) "Verificado ✅"
                                    else "Não verificado"
                                }"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.logout() }) {
                        Text("Logout")
                    }
                }
            } ?: Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nenhum usuário conectado.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.loadProfile() }) {
                    Text("Tentar novamente")
                }
            }
        }
    }
}
