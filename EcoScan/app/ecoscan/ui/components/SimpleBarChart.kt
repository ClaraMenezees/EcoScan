package com.ifpe.ecoscan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement

@Composable
fun SimpleBarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    barWidth: Dp = 28.dp,
    spacing: Dp = 12.dp,
    barColor: Color = Color(0xFF4CAF50)
) {
    if (data.isEmpty()) {
        Text("Sem dados", fontSize = 12.sp)
        return
    }

    val max = data.maxOf { it.second }.coerceAtLeast(1f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.height(140.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                    modifier = Modifier
                        .width(barWidth)
                        .fillMaxHeight(0.9f)
                ) {
                    val heightRatio = (value / max).coerceIn(0f, 1f)
                    val barHeight = size.height * heightRatio
                    val top = size.height - barHeight

                    // drawRect padrão do DrawScope (nenhum import especial)
                    drawRect(
                        color = barColor,
                        topLeft = Offset(0f, top),
                        size = Size(size.width, barHeight)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = label, fontSize = 12.sp)
            }
        }
    }
}
