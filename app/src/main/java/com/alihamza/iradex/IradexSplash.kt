package com.alihamza.iradex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun IradexSplash(onFinished: () -> Unit) {
    var logoVisible by remember { mutableStateOf(false) }
    var nameVisible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.72f,
        animationSpec = tween(650),
        label = "splashLogoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "splashLogoAlpha"
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(650)
        nameVisible = true
        delay(1100)
        onFinished()
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.radialGradient(
                listOf(Color(0xFF17122A), IradexColors.Background, Color(0xFF050609))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(138.dp).scale(logoScale).alpha(logoAlpha),
                shape = RoundedCornerShape(42.dp),
                color = Color(0xFF111522),
                shadowElevation = 28.dp
            ) {
                Box(
                    Modifier.background(Brush.radialGradient(listOf(Color(0x338B7CFF), Color.Transparent))),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.ic_iradex), null, modifier = Modifier.size(112.dp))
                }
            }
            Spacer(Modifier.height(30.dp))
            AnimatedVisibility(
                visible = nameVisible,
                enter = fadeIn(tween(550)) + slideInVertically(tween(550)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("IRADEX", color = IradexColors.Text, fontSize = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = 6.sp)
                    Text("ONE INTENTION AT A TIME", color = IradexColors.PrimarySoft, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                }
            }
        }
    }
}
