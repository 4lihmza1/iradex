package com.alihamza.iradex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun IradexSplash(onFinished: () -> Unit) {
    var markVisible by remember { mutableStateOf(false) }
    var nameVisible by remember { mutableStateOf(false) }
    val markScale by animateFloatAsState(
        targetValue = if (markVisible) 1f else 0.8f,
        animationSpec = tween(520),
        label = "markScale"
    )
    val markAlpha by animateFloatAsState(
        targetValue = if (markVisible) 1f else 0f,
        animationSpec = tween(420),
        label = "markAlpha"
    )
    val letterSpacing by animateFloatAsState(
        targetValue = if (nameVisible) 6f else 12f,
        animationSpec = tween(600),
        label = "wordmarkSpacing"
    )

    LaunchedEffect(Unit) {
        markVisible = true
        delay(430)
        nameVisible = true
        delay(900)
        onFinished()
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.radialGradient(
                0.0f to Color(0xFF1C1533),
                0.38f to Color(0xFF0D0C14),
                1.0f to Color(0xFF050609)
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(144.dp).scale(markScale).alpha(markAlpha)
                    .background(
                        Brush.radialGradient(listOf(Color(0x448B7CFF), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                IradexMark(Modifier.size(104.dp))
            }
            Spacer(Modifier.height(22.dp))
            AnimatedVisibility(
                visible = nameVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "IRADEX",
                        color = IradexColors.Text,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = letterSpacing.sp
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "INTENTION, ACTIVATED",
                        color = IradexColors.PrimarySoft,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IradexMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val violet = Color(0xFF8067FF)
        val lavender = Color(0xFFB29FFF)
        val coral = Color(0xFFFF645F)
        val mainWidth = size.minDimension * 0.13f
        val legWidth = size.minDimension * 0.11f

        drawLine(
            color = violet.copy(alpha = 0.2f),
            start = Offset(size.width * 0.22f, size.height * 0.31f),
            end = Offset(size.width * 0.50f, size.height * 0.57f),
            strokeWidth = mainWidth * 1.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = violet.copy(alpha = 0.2f),
            start = Offset(size.width * 0.50f, size.height * 0.57f),
            end = Offset(size.width * 0.78f, size.height * 0.31f),
            strokeWidth = mainWidth * 1.8f,
            cap = StrokeCap.Round
        )
        drawLine(violet, Offset(size.width * 0.22f, size.height * 0.31f), Offset(size.width * 0.50f, size.height * 0.57f), mainWidth, StrokeCap.Round)
        drawLine(violet, Offset(size.width * 0.50f, size.height * 0.57f), Offset(size.width * 0.78f, size.height * 0.31f), mainWidth, StrokeCap.Round)
        drawLine(lavender, Offset(size.width * 0.42f, size.height * 0.70f), Offset(size.width * 0.32f, size.height * 0.80f), legWidth, StrokeCap.Round)
        drawLine(lavender, Offset(size.width * 0.58f, size.height * 0.70f), Offset(size.width * 0.68f, size.height * 0.80f), legWidth, StrokeCap.Round)
        drawArc(
            color = coral,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width * 0.42f, size.height * 0.17f),
            size = Size(size.width * 0.16f, size.height * 0.14f)
        )
    }
}
