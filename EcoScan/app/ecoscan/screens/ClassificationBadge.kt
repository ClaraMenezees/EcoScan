package com.ifpe.ecoscan.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ifpe.ecoscan.ui.theme.EcoGreen
import com.ifpe.ecoscan.ui.theme.EcoAmber
import com.ifpe.ecoscan.ui.theme.EcoRed
import com.ifpe.ecoscan.ui.theme.EcoMuted

@Composable
fun ClassificationBadge(
    label: String,
    reason: String? = null,
    confidence: Double? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    val (bg, fg) = when (label.lowercase()) {
        "saudável", "saudavel" -> Pair(EcoGreen, MaterialTheme.colorScheme.onPrimary)
        "moderado" -> Pair(EcoAmber, MaterialTheme.colorScheme.onSurface)
        "prejudicial" -> Pair(EcoRed, MaterialTheme.colorScheme.onPrimary)
        else -> Pair(EcoMuted, MaterialTheme.colorScheme.onSurface)
    }

    Row(
        modifier = Modifier
            .background(color = bg, shape = RoundedCornerShape(12.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        confidence?.let {
            Text(text = "  • ${(it*100).toInt()}%", color = fg, fontSize = 12.sp)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Fechar") }
            },
            title = { Text(text = label) },
            text = {
                Text(reason ?: "Motivo não disponível")
            }
        )
    }
}
