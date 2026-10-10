package com.example.final_exam_109.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PinkPrimary = Color(0xFFE91E78)
val PinkDark = Color(0xFFC2185B)
val PinkSoft = Color(0xFFFF8FB8)
val PinkLight = Color(0xFFFFE4EF)
val PinkBorder = Color(0xFFF3B4CE)
val BgTop = Color(0xFFFFF5F9)
val BgBottom = Color(0xFFFFD9E8)
val TextDark = Color(0xFF3A1F2B)

val IncomeGreen = Color(0xFF2E9E6B)
val IncomeBg = Color(0xFFE3F6EC)
val ExpenseRed = Color(0xFFE53957)
val ExpenseBg = Color(0xFFFFE3E8)

private val PinkColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkLight,
    onPrimaryContainer = PinkDark,
    background = BgTop,
    surface = Color.White,
    onSurface = TextDark
)

@Composable
fun PinkLedgerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PinkColorScheme, content = content)
}