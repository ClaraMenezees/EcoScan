package com.ifpe.ecoscan.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.screens.ClassificationBadge
import com.ifpe.ecoscan.screens.NutritionalSection
import com.ifpe.ecoscan.utils.ClassificationUtils
import com.ifpe.ecoscan.utils.IngredientAnalysis
import com.ifpe.ecoscan.viewmodel.HistoryViewModel
import com.ifpe.ecoscan.viewmodel.ProductDetailViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler

/**
 * Tela de detalhes do produto — layout moderno e ecológico.
 * - Responsivo
 * - Acessível
 * - Explicativo (badge + motivo)
 */

@Composable
fun ProductDetailScreen(
    barcode: String,
    viewModel: ProductDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    historyVm: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val product by viewModel.product.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(barcode) { viewModel.loadProduct(barcode) }

    // salva no histórico quando carregado
    LaunchedEffect(product) { product?.let { historyVm.addProduct(it) } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        product?.let { p ->
            ProductDetailContent(product = p)
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ProductDetailContent(product: Product) {
    val scrollState = rememberScrollState()
    val ctx = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // classificação combinada (label, motivo, confiança)
    val classification = remember(product) {
        ClassificationUtils.classifyProduct(
            nutriments = product.nutriments,
            nutritionGradeRaw = product.nutritionGrade,
            ecoscore = product.ecoscoreGrade,
            ingredientsText = product.ingredientsText
        )
    }

    // ingredientes analisados (lista de riscos)
    val riskyIngredients = remember(product) { IngredientAnalysis.analyzeIngredients(product.ingredientsText) }

    // favorito local (apenas UI local; você pode persistir em DataStore/Room)
    var isFavorite by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Header: nome + marca + ações
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name ?: "Produto desconhecido",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.brand ?: "Marca não informada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.semantics { contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos" }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {
                    // compartilhar (texto simples + link para OpenFoodFacts se existir)
                    val shareText = buildString {
                        append("${product.name ?: "Produto"}\n")
                        product.brand?.let { append("Marca: $it\n") }
                        product.ingredientsText?.let { append("Ingredientes: ${it.take(200)}...\n") }
                        append("Classificação: ${classification.label} — ${"%.0f".format(classification.confidence * 100)}% confiança")
                        product.barcode?.let { append("\nhttps://world.openfoodfacts.org/product/$it") }
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    ctx.startActivity(Intent.createChooser(intent, "Compartilhar produto"))
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Compartilhar produto")
                }
            }
        }

        // --- Imagem destacada em card
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp, max = 300.dp)
                .clip(RoundedCornerShape(10.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (!product.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = "Imagem do produto ${product.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder simples com emoji e cor ecológica suave
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿 Sem imagem disponível", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        // --- Classification badge + short reason
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ClassificationBadge(
                label = classification.label,
                reason = classification.reason,
                confidence = classification.confidence
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = when {
                    classification.confidence >= 0.8 -> "Confiança alta"
                    classification.confidence >= 0.5 -> "Confiança média"
                    else -> "Confiança baixa"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- Quick facts: açúcar / gord sat / sódio / fibra
        QuickFactsRow(nutriments = product.nutriments)

        // --- Ingredientes (expansível)
        ExpandableSection(title = "Ingredientes", defaultExpanded = false) {
            Text(
                text = product.ingredientsText ?: "Não informado pelo fabricante",
                style = MaterialTheme.typography.bodyMedium
            )

            if (riskyIngredients.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("⚠ Ingredientes de risco detectados:", style = MaterialTheme.typography.titleSmall)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    riskyIngredients.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }

        // --- Seção Nutricional detalhada (tabela ou texto)
        ExpandableSection(title = "Informação nutricional (por 100g)", defaultExpanded = true) {
            NutritionalSection(product.nutriments)
        }

        // --- Ações secundárias
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = {
                // abrir página do produto no OpenFoodFacts (se barcode disponível)
                product.barcode?.let {
                    val url = "https://world.openfoodfacts.org/product/$it"
                    uriHandler.openUri(url)
                }
            }) {
                Text(text = "Ver no OpenFoodFacts")
            }


        }

        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Linha de fatos rápidos (pequenos "chips" com valores)
 */
@Composable
private fun QuickFactsRow(nutriments: com.ifpe.ecoscan.model.Nutriments?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickFactChip(label = "🍬 Açúcar", value = nutriments?.sugars100g?.let { "%.1f g".format(it) } ?: "N/A")
        QuickFactChip(label = "🥓 Gord. sat.", value = nutriments?.saturatedFat100g?.let { "%.1f g".format(it) } ?: "N/A")
        QuickFactChip(label = "🧂 Sódio", value = nutriments?.sodium100g?.let {
            // se estiver em g, converte para mg para leitura
            val v = if (it > 1000.0) it else it * 1000.0
            "${v.toInt()} mg"
        } ?: "N/A")
        QuickFactChip(label = "🌾 Fibras", value = nutriments?.fiber100g?.let { "%.1f g".format(it) } ?: "N/A")
    }
}

@Composable
private fun QuickFactChip(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.wrapContentWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 8.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Composable expansível simples (title + content)
 */
@Composable
private fun ExpandableSection(title: String, defaultExpanded: Boolean = false, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(if (expanded) "Ocultar ▲" else "Abrir ▼", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }

        if (expanded) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                content()
            }
        }
    }
}
