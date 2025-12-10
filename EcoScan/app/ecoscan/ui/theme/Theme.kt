package com.ifpe.ecoscan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color   // 👈 IMPORT NECESSÁRIO
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = EcoGreen,
    onPrimary = Color.White,
    secondary = EcoLeaf,
    onSecondary = Color.Black,
    background = EcoBackground,
    surface = EcoSurface,
    error = EcoRed,
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoGreenDark,
    onPrimary = Color.White,
    secondary = EcoLeaf,
    background = Color(0xFF0F1F12),
    surface = Color(0xFF111827),
    error = EcoRed
)

@Composable
fun EcoScanTheme(
    useDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
        ),
        shapes = Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
        ),
        content = content
    )
}
