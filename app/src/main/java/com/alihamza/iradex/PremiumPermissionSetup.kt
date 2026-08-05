package com.alihamza.iradex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PremiumPermissionSetup(
    notificationsReady: Boolean,
    exactAlarmReady: Boolean,
    fullScreenReady: Boolean,
    onNotifications: () -> Unit,
    onExactAlarm: () -> Unit,
    onFullScreen: () -> Unit,
    onComplete: () -> Unit,
    onLater: () -> Unit
) {
    val step = when {
        !notificationsReady -> 0
        !exactAlarmReady -> 1
        !fullScreenReady -> 2
        else -> 3
    }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17122A), IradexColors.Background, IradexColors.Background)
                )
            ).padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(4) { index ->
                Box(
                    Modifier.padding(horizontal = 4.dp).width(if (index == step) 28.dp else 8.dp).height(8.dp)
                        .background(if (index <= step) IradexColors.Primary else IradexColors.Border, CircleShape)
                )
            }
        }
        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 3 } + fadeOut())
            },
            label = "permissionSetup"
        ) { current ->
            when (current) {
                0 -> PermissionStage(
                    eyebrow = "STEP 1 OF 3",
                    icon = Icons.Default.NotificationsActive,
                    title = "First, let Iradex reach you.",
                    copy = "Notifications carry the commitment alarm and show the task you chose.",
                    systemCopy = "Android will show its standard Allow notification popup.",
                    button = "Enable notifications",
                    opensSettings = false,
                    onClick = onNotifications
                )
                1 -> PermissionStage(
                    eyebrow = "STEP 2 OF 3",
                    icon = Icons.Default.Alarm,
                    title = "Make the moment exact.",
                    copy = "Exact-alarm access lets Iradex ring at the time you selected—even while the phone is idle.",
                    systemCopy = "Android requires the Alarms & reminders settings page. Enable Iradex, then return here.",
                    button = "Open alarm access",
                    opensSettings = true,
                    onClick = onExactAlarm
                )
                2 -> PermissionStage(
                    eyebrow = "STEP 3 OF 3",
                    icon = Icons.Default.PhoneAndroid,
                    title = "Let the alarm take the screen.",
                    copy = "Full-screen access allows a scheduled commitment alarm to appear over the lock screen.",
                    systemCopy = "Android requires its Full-screen alerts settings page. Enable Iradex, then return here.",
                    button = "Open full-screen access",
                    opensSettings = true,
                    onClick = onFullScreen
                )
                else -> ReadyStage(onComplete)
            }
        }
        if (step < 3) {
            OutlinedButton(
                onClick = onLater,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFF343743)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = IradexColors.Muted)
            ) {
                Text("Set up later", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PermissionStage(
    eyebrow: String,
    icon: ImageVector,
    title: String,
    copy: String,
    systemCopy: String,
    button: String,
    opensSettings: Boolean,
    onClick: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(top = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(eyebrow, color = IradexColors.PrimarySoft, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(32.dp))
        Surface(
            Modifier.size(132.dp),
            shape = RoundedCornerShape(42.dp),
            color = Color(0xFF1C1B2B),
            border = BorderStroke(1.dp, Color(0xFF403A68))
        ) {
            Box(
                Modifier.background(Brush.radialGradient(listOf(Color(0x558B7CFF), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = IradexColors.PrimarySoft, modifier = Modifier.size(56.dp))
            }
        }
        Spacer(Modifier.height(34.dp))
        Text(title, color = IradexColors.Text, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(copy, color = IradexColors.Muted, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFF15171D), border = BorderStroke(1.dp, Color(0xFF2D3039))) {
            Text(systemCopy, color = Color(0xFFB8BBC4), fontSize = 12.sp, lineHeight = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Primary)
        ) {
            Text(button, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(if (opensSettings) Icons.Default.OpenInNew else Icons.Default.ArrowForward, null)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ReadyStage(onComplete: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(Modifier.size(140.dp), shape = CircleShape, color = Color(0xFF18372C)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, tint = IradexColors.Success, modifier = Modifier.size(68.dp))
            }
        }
        Spacer(Modifier.height(34.dp))
        Text("Iradex can reach you.", color = IradexColors.Text, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text("Notifications, exact timing and full-screen alerts are ready.", color = IradexColors.Muted, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
        Spacer(Modifier.height(38.dp))
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Success, contentColor = Color(0xFF07150F))
        ) {
            Text("Create my first commitment", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}
