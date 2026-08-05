package com.alihamza.iradex

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

private val HomeBlack = Color(0xFF07080B)
private val Glass = Color(0xE6161820)
private val GlassSoft = Color(0xB31A1C24)
private val GlassBorder = Color(0xFF343743)
private val CoralGlow = Color(0xFFFF706C)

@Composable
fun PremiumHomeScreen(
    commitment: Commitment?,
    onCreate: () -> Unit,
    onProof: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val streak = IradexStorage.currentStreak(context)
    val completionRate = IradexStorage.completionRate(context)
    val completed = IradexStorage.history(context).count { !it.partial }
    val momentum = momentumScore(completionRate, streak, completed)

    Box(Modifier.fillMaxSize().background(HomeBlack)) {
        Image(
            painter = painterResource(R.drawable.iradex_intent_core),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(590.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
        Box(
            Modifier.fillMaxWidth().height(610.dp).background(
                Brush.verticalGradient(
                    0.0f to Color(0x24000000),
                    0.42f to Color.Transparent,
                    0.72f to Color(0x9907080B),
                    1.0f to HomeBlack
                )
            )
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp)
        ) {
            PremiumHeader(onSettings)
            Spacer(Modifier.height(292.dp))
            MomentumHero(momentum, completed)
            Spacer(Modifier.height(20.dp))
            MetricRow(streak, completionRate, completed)
            Spacer(Modifier.height(24.dp))
            CommitmentGlassCard(commitment, onCreate, onProof)
            Spacer(Modifier.height(28.dp))
            ReflectionCard(commitment != null, completed)
            Spacer(Modifier.height(24.dp))
        }
        PremiumBottomBar(
            onHome = {},
            onHistory = onHistory,
            onSettings = onSettings,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PremiumHeader(onSettings: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "IRADEX",
                color = IradexColors.Text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                "ONE INTENTION AT A TIME",
                color = IradexColors.PrimarySoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp
            )
        }
        Surface(
            onClick = onSettings,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color(0x99161920),
            border = BorderStroke(1.dp, Color(0x663D414D))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Settings, "Settings", tint = IradexColors.Text)
            }
        }
    }
}

@Composable
private fun MomentumHero(score: Int?, completed: Int) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (score == null) "YOUR INTENT CORE" else "MOMENTUM SCORE",
            color = IradexColors.PrimarySoft,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.2.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                score?.toString() ?: "READY",
                color = IradexColors.Text,
                fontSize = if (score == null) 46.sp else 64.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp
            )
            if (score != null) {
                Icon(
                    Icons.Default.Bolt,
                    null,
                    tint = IradexColors.Success,
                    modifier = Modifier.padding(start = 4.dp).size(24.dp)
                )
            }
        }
        Text(
            if (completed == 0) "Complete one honest step to activate your score."
            else "Built from your follow-through, streak and completed proofs.",
            color = IradexColors.Muted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MetricRow(streak: Int, completionRate: Int, completed: Int) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricPill(Icons.Default.LocalFireDepartment, streak.toString(), "STREAK", Color(0xFFFFC562), Modifier.weight(1f))
        MetricPill(Icons.Default.Bolt, "$completionRate%", "FOLLOW", IradexColors.Success, Modifier.weight(1f))
        MetricPill(Icons.Default.CheckCircle, completed.toString(), "PROOFS", IradexColors.PrimarySoft, Modifier.weight(1f))
    }
}

@Composable
private fun MetricPill(icon: ImageVector, value: String, label: String, tint: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassSoft,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(5.dp))
                Text(value, color = IradexColors.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(label, color = IradexColors.Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 7.dp))
    }
}

@Composable
private fun CommitmentGlassCard(commitment: Commitment?, onCreate: () -> Unit, onProof: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(30.dp),
        color = Glass,
        border = BorderStroke(1.dp, GlassBorder),
        shadowElevation = 18.dp
    ) {
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF28213E)) {
                    Text(
                        if (commitment == null) "NEXT STEP" else commitment.category.uppercase(),
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = IradexColors.PrimarySoft,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                if (commitment != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = CoralGlow, modifier = Modifier.size(17.dp))
                        Text(
                            String.format(Locale.getDefault(), "%02d:%02d", commitment.alarmHour, commitment.alarmMinute),
                            Modifier.padding(start = 6.dp),
                            color = IradexColors.Text,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                commitment?.task ?: "Turn one intention into visible progress.",
                color = IradexColors.Text,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (commitment == null) "Choose a small task, set the moment, then prove the step you made."
                else "Stay with this one task until you have made a step you can prove.",
                color = IradexColors.Muted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = if (commitment == null) onCreate else onProof,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IradexColors.Primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (commitment == null) "Create commitment" else "Submit progress",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(10.dp))
                Icon(if (commitment == null) Icons.Default.Add else Icons.Default.ArrowForward, null)
            }
        }
    }
}

@Composable
private fun ReflectionCard(hasCommitment: Boolean, completed: Int) {
    Surface(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF111319),
        border = BorderStroke(1.dp, Color(0xFF242730))
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(42.dp), shape = CircleShape, color = Color(0xFF20232C)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bolt, null, tint = IradexColors.PrimarySoft)
                }
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(
                    when {
                        hasCommitment -> "Your next move is already chosen."
                        completed > 0 -> "Momentum waits for the next honest step."
                        else -> "Small proof beats a perfect plan."
                    },
                    color = IradexColors.Text,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Iradex measures action—not screen time.",
                    color = IradexColors.Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumBottomBar(
    onHome: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier.padding(horizontal = 38.dp, vertical = 12.dp).fillMaxWidth().height(66.dp),
        shape = RoundedCornerShape(38.dp),
        color = Color(0xF21B1D22),
        border = BorderStroke(1.dp, Color(0xFF3A3D46)),
        shadowElevation = 20.dp
    ) {
        Row(Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            NavItem(Icons.Default.Home, "Home", true, onHome, Modifier.weight(1f))
            NavItem(Icons.Default.History, "Progress", false, onHistory, Modifier.weight(1f))
            NavItem(Icons.Default.Settings, "Settings", false, onSettings, Modifier.weight(1f))
        }
    }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(30.dp))
            .background(if (selected) Color(0xFF34363C) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, label, tint = if (selected) Color.White else IradexColors.Muted, modifier = Modifier.size(21.dp))
        if (selected) {
            Spacer(Modifier.width(7.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

private fun momentumScore(completionRate: Int, streak: Int, completed: Int): Int? {
    if (completed == 0) return null
    val consistency = min(streak, 7) / 7f * 25f
    val evidence = min(completed, 5) / 5f * 15f
    return (completionRate * 0.60f + consistency + evidence).roundToInt().coerceIn(1, 100)
}
