package com.ifpe.ecoscan.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ifpe.ecoscan.R
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.ui.theme.EcoGreen
import com.ifpe.ecoscan.viewmodel.FavoritesViewModel
import com.ifpe.ecoscan.viewmodel.FavoritesViewModelFactory
import com.ifpe.ecoscan.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onProductClick: (Product) -> Unit = {}
) {
    val context = LocalContext.current

    // Estado da Home
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // sempre que voltar pra Home, recarrega o resumo
    LaunchedEffect(Unit) {
        viewModel.loadSummary()
    }

    // Favorites
    val favVm: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(context.applicationContext)
    )
    LaunchedEffect(favVm) { favVm.observeFavorites() }
    val favs by favVm.favorites.collectAsState(initial = emptySet())

    // DICAS ECOLÓGICAS
    val ecoTips = listOf(
        "Prefira alimentos minimamente processados. Menos embalagem, menos impacto ambiental.",
        "Evite produtos com muitos aditivos químicos. Ingredientes simples geralmente são mais saudáveis.",
        "Levar sua própria sacola reutilizável reduz o consumo de plástico descartável.",
        "Consumir produtos locais diminui a emissão de carbono relacionada ao transporte.",
        "Leia os rótulos: menos ingredientes desconhecidos costuma indicar um produto melhor.",
        "Evite o desperdício de alimentos. Planejar compras ajuda o meio ambiente e o bolso.",
        "Priorize marcas que se comprometem com sustentabilidade e produção consciente."
    )

    val tipOfTheDay = remember {
        val dayOfYear = java.util.Calendar.getInstance()
            .get(java.util.Calendar.DAY_OF_YEAR)
        ecoTips[dayOfYear % ecoTips.size]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1) HERO SECTION – card verde com título + botão escanear + logo
        HeroSection(
            greeting = state.greeting,
            onScanClick = onScanClick
        )

        // 2) ROW de cards (último escaneado, favoritos, histórico)
        Text(
            text = "Resumo rápido",
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // Último escaneado
            item {
                val last = state.lastProduct
                SummaryCard(
                    title = "Último escaneado",
                    subtitle = last?.name ?: "Nenhum produto ainda",
                    background = Color(0xFF4CAF50),
                    onClick = {
                        if (last != null) onProductClick(last)
                    }
                )
            }

            // Favoritos
            item {
                SummaryCard(
                    title = "Favoritos",
                    subtitle = "${favs.size} itens salvos",
                    background = Color(0xFFFFC857),
                    onClick = {
                        // futuro: tela de favoritos
                    }
                )
            }

            // Resumo do histórico
            item {
                SummaryCard(
                    title = "Histórico",
                    subtitle = "${state.historyCount} produtos",
                    background = Color(0xFF3F51B5),
                    onClick = onHistoryClick
                )
            }
        }

        // Ações rápidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            ) { Text("Perfil") }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.weight(1f)
            ) { Text("Sair") }
        }

        // Card com dica ecológica
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Dica ecológica de hoje 🌱",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = tipOfTheDay,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (state.historyCount == 0) {
                    Text(
                        "Comece escaneando um produto para aumentar seu impacto positivo!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF388E3C)
                    )
                } else {
                    Text(
                        "Você já avaliou ${state.historyCount} produto(s). Continue fazendo escolhas conscientes!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF388E3C)
                    )
                }
            }
        }
    }
}

/** Card grande superior com o texto de boas-vindas, botão ESCANEAR e LOGO */
@Composable
private fun HeroSection(
    greeting: String,
    onScanClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreen
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = greeting,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Text(
                text = "Escaneie rótulos e descubra quão sustentável e saudável é cada produto.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEAF7EC)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = onScanClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = EcoGreen
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f)
                ) {
                    Text("Escanear produto")
                }

                Spacer(Modifier.width(12.dp))

                // LOGO NO CARD
                Image(
                    painter = painterResource(id = R.drawable.ecoscan_logo),
                    contentDescription = "Logo EcoScan",
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    subtitle: String,
    background: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(210.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFF5F5F5),
                maxLines = 2
            )
        }
    }
}
