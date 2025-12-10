package com.ifpe.ecoscan.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifpe.ecoscan.model.Nutriments

@Composable
fun NutritionalSection(nutriments: Nutriments?) {
    if (nutriments == null) {
        Text(
            text = "Informações nutricionais não disponíveis",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    Column {
        Text(
            text = "Informação Nutricional (por 100g)",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        // Açúcar
        Text("Açúcar: ${nutriments.sugars100g ?: "—"} g")

        // Gordura total
        Text("Gordura total: ${nutriments.fat100g ?: "—"} g")

        // Gordura saturada
        Text("Gordura saturada: ${nutriments.saturatedFat100g ?: "—"} g")

        // Fibras
        Text("Fibras: ${nutriments.fiber100g ?: "—"} g")

        // Proteínas
        Text("Proteínas: ${nutriments.proteins100g ?: "—"} g")

        // Sódio
        Text("Sódio: ${nutriments.sodium100g ?: "—"} mg")

        // Sal (se existir)
        nutriments.salt100g?.let {
            Text("Sal: $it g")
        }
    }
}
