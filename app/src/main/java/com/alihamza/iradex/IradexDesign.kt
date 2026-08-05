package com.alihamza.iradex

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object IradexColors {
    val Background = Color(0xFF090A0D)
    val Surface = Color(0xFF15171D)
    val SurfaceRaised = Color(0xFF1D2028)
    val Border = Color(0xFF2B2E37)
    val Primary = Color(0xFF8B7CFF)
    val PrimarySoft = Color(0xFFB7AEFF)
    val Accent = Color(0xFFC9FF72)
    val Text = Color(0xFFF7F7F8)
    val Muted = Color(0xFFA5A8B2)
    val Success = Color(0xFF56E39F)
    val Danger = Color(0xFFFF667A)
}

private val iradexTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1.4f).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 39.sp,
        letterSpacing = (-0.7f).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        lineHeight = 33.sp,
        letterSpacing = (-0.4f).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
)

@Composable
fun IradexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = IradexColors.Primary,
            onPrimary = Color.White,
            secondary = IradexColors.Accent,
            background = IradexColors.Background,
            onBackground = IradexColors.Text,
            surface = IradexColors.Surface,
            onSurface = IradexColors.Text,
            error = IradexColors.Danger
        ),
        typography = iradexTypography,
        content = content
    )
}
