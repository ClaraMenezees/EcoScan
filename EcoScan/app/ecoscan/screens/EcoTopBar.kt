package com.ifpe.ecoscan.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ifpe.ecoscan.R
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)   // 👈 ISSO TIRA O AVISO “API experimental”
@Composable
fun EcoTopBar(
    currentRoute: String?,
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    TopAppBar(
        title = {
            when (currentRoute) {
                "home" -> {
                    // Logo + texto no topo (somente na Home)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ecoscan_logo),
                            contentDescription = "Logo EcoScan",
                            modifier = Modifier.width(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("EcoScan")
                    }
                }
                "history" -> Text("Histórico")
                "profile" -> Text("Perfil")
                "scanner" -> Text("Scanner")
                else -> Text("EcoScan")
            }
        },
        navigationIcon = {
            if (currentRoute != "home") {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
            }
        },
        actions = {
            if (currentRoute == "home") {
                IconButton(onClick = onHistory) {
                    Icon(Icons.Default.History, contentDescription = "Histórico")
                }
                IconButton(onClick = onProfile) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil")
                }
            }
        }
    )
}
