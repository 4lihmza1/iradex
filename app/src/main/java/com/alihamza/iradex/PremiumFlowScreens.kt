package com.alihamza.iradex

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val FlowCard = Color(0xE6161820)
private val FlowBorder = Color(0xFF343743)

@Composable
fun PremiumProofScreen(
    commitment: Commitment?,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var captured by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    val noteOnly = commitment?.proofMethod == "Quick progress note"

    fun proofUri(): Uri {
        val directory = File(context.cacheDir, "proofs").apply { mkdirs() }
        val file = File(directory, "proof_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        captured = success
    }
    fun openCamera() {
        proofUri().also { uri ->
            pendingUri = uri
            camera.launch(uri)
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) openCamera()
    }

    Column(
        Modifier.fillMaxSize().navigationBarsPadding().background(
            Brush.verticalGradient(listOf(Color(0xFF141027), IradexColors.Background, IradexColors.Background))
        ).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(bottom = 30.dp)
    ) {
        PremiumTopBar("Submit progress", onBack)
        Spacer(Modifier.height(28.dp))
        Text("PROOF, NOT PERFECTION", color = IradexColors.PrimarySoft, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(10.dp))
        Text("Show what moved forward.", color = IradexColors.Text, style = MaterialTheme.typography.headlineLarge)
        Text(
            commitment?.task ?: "Capture the step you made.",
            color = IradexColors.Muted,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 10.dp, bottom = 26.dp)
        )

        if (!noteOnly) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(260.dp).clickable {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        openCamera()
                    } else {
                        cameraPermission.launch(Manifest.permission.CAMERA)
                    }
                },
                shape = RoundedCornerShape(30.dp),
                color = if (captured) Color(0xFF142D25) else FlowCard,
                border = BorderStroke(1.dp, if (captured) IradexColors.Success else FlowBorder)
            ) {
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.radialGradient(
                            listOf(
                                if (captured) Color(0x3356E39F) else Color(0x338B7CFF),
                                Color.Transparent
                            )
                        )
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            Modifier.size(84.dp),
                            shape = CircleShape,
                            color = if (captured) IradexColors.Success else IradexColors.Primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (captured) Icons.Default.Check else Icons.Default.CameraAlt,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Text(
                            if (captured) "Fresh proof captured" else "Open camera",
                            color = IradexColors.Text,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            if (captured) "Tap to retake" else "Gallery uploads are disabled for honest, fresh proof",
                            color = IradexColors.Muted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 7.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        OutlinedTextField(
            value = note,
            onValueChange = { if (it.length <= 160) note = it },
            label = { Text(if (noteOnly) "What changed?" else "Optional reflection") },
            placeholder = { Text("Describe the honest step you made") },
            modifier = Modifier.fillMaxWidth(),
            minLines = if (noteOnly) 5 else 3,
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = IradexColors.Text,
                unfocusedTextColor = IradexColors.Text,
                focusedBorderColor = IradexColors.Primary,
                unfocusedBorderColor = FlowBorder,
                focusedContainerColor = FlowCard,
                unfocusedContainerColor = FlowCard,
                focusedLabelColor = IradexColors.PrimarySoft,
                unfocusedLabelColor = IradexColors.Muted,
                cursorColor = IradexColors.Primary
            )
        )
        Spacer(Modifier.height(26.dp))
        Button(
            onClick = onComplete,
            enabled = if (noteOnly) note.trim().length >= 3 else captured,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IradexColors.Success,
                contentColor = Color(0xFF07150F),
                disabledContainerColor = Color(0xFF24272F),
                disabledContentColor = Color(0xFF686C74)
            )
        ) {
            Icon(Icons.Default.Verified, null)
            Spacer(Modifier.width(8.dp))
            Text("Complete commitment", fontWeight = FontWeight.Bold)
        }
        Text(
            if (noteOnly) "An honest small step counts. Perfection is never required."
            else "Iradex verifies fresh capture—not whether your work is perfect.",
            color = IradexColors.Muted,
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 13.dp)
        )
    }
}

@Composable
fun PremiumSuccessScreen(onDone: () -> Unit) {
    Box(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().background(
            Brush.radialGradient(
                0.0f to Color(0xFF183E31),
                0.48f to Color(0xFF0D1B17),
                1.0f to IradexColors.Background
            )
        )
    ) {
        Column(
            Modifier.fillMaxSize().padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(Modifier.size(172.dp), shape = CircleShape, color = Color(0x221BFF9A), border = BorderStroke(1.dp, Color(0x4456E39F))) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(Modifier.size(112.dp), shape = CircleShape, color = Color(0xFF193B30)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = IradexColors.Success, modifier = Modifier.size(58.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(34.dp))
            Text("PROOF RECORDED", color = IradexColors.Success, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(10.dp))
            Text("Momentum created.", color = IradexColors.Text, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
            Text(
                "You followed through on one intention. That evidence matters more than a perfect plan.",
                color = IradexColors.Muted,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp)
            )
            Spacer(Modifier.height(38.dp))
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Success, contentColor = Color(0xFF07150F))
            ) {
                Text("Return to my Intent Core", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
        }
    }
}

@Composable
fun PremiumHistoryScreen(items: List<HistoryItem>, onBack: () -> Unit) {
    val context = LocalContext.current
    val streak = IradexStorage.currentStreak(context)
    val rate = IradexStorage.completionRate(context)
    val completed = items.count { !it.partial }
    val score = calculateMomentumScore(rate, streak, completed)

    Column(
        Modifier.fillMaxSize().background(IradexColors.Background)
            .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(bottom = 30.dp)
    ) {
        PremiumTopBar("Progress", onBack)
        Spacer(Modifier.height(18.dp))
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            color = FlowCard,
            border = BorderStroke(1.dp, FlowBorder)
        ) {
            Column(
                Modifier.background(Brush.radialGradient(listOf(Color(0x337C63FF), Color.Transparent))).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("MOMENTUM SCORE", color = IradexColors.PrimarySoft, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                Text(score?.toString() ?: "—", color = IradexColors.Text, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (score == null) "Complete your first commitment to activate your score."
                    else "60% follow-through · 25% streak · 15% proof volume",
                    color = IradexColors.Muted,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MiniMetric(Icons.Default.LocalFireDepartment, streak.toString(), "streak", Color(0xFFFFC562))
                    MiniMetric(Icons.Default.AutoGraph, "$rate%", "follow-through", IradexColors.Success)
                    MiniMetric(Icons.Default.CheckCircle, completed.toString(), "proofs", IradexColors.PrimarySoft)
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Text("EVIDENCE OF ACTION", color = IradexColors.Text, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(12.dp))
        if (items.isEmpty()) {
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = Color(0xFF12141A), border = BorderStroke(1.dp, Color(0xFF262932))) {
                Column(Modifier.padding(vertical = 52.dp, horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, tint = IradexColors.PrimarySoft, modifier = Modifier.size(48.dp))
                    Text("Your proof trail starts here", color = IradexColors.Text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                    Text("Every completed commitment becomes private evidence of follow-through.", color = IradexColors.Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                }
            }
        } else {
            items.forEach { item -> HistoryCard(item) }
        }
    }
}

@Composable
private fun HistoryCard(item: HistoryItem) {
    Surface(
        Modifier.fillMaxWidth().padding(bottom = 11.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF14161C),
        border = BorderStroke(1.dp, Color(0xFF292C34))
    ) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                Modifier.size(46.dp),
                shape = RoundedCornerShape(15.dp),
                color = if (item.partial) Color(0xFF3D321D) else Color(0xFF17342A)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (item.partial) Icons.Default.HourglassBottom else Icons.Default.Check,
                        null,
                        tint = if (item.partial) Color(0xFFFFC562) else IradexColors.Success
                    )
                }
            }
            Column(Modifier.weight(1f).padding(start = 13.dp)) {
                Text(item.task, color = IradexColors.Text, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.category} · ${SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(item.completedAt))}",
                    color = IradexColors.Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(if (item.proofMethod.startsWith("Photo")) Icons.Default.CameraAlt else Icons.Default.EditNote, null, tint = IradexColors.PrimarySoft)
        }
    }
}

@Composable
fun PremiumSettingsScreen(
    hasCommitment: Boolean,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onRequestNotifications: () -> Unit,
    onTestAlarm: () -> Unit
) {
    val context = LocalContext.current
    var showCancelConfirmation by remember { mutableStateOf(false) }
    val notificationsReady = Build.VERSION.SDK_INT < 33 ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val exactReady = AlarmScheduler.canSchedule(context)
    val fullScreenReady = if (Build.VERSION.SDK_INT >= 34) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).canUseFullScreenIntent()
    } else true

    Column(
        Modifier.fillMaxSize().background(IradexColors.Background)
            .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(bottom = 32.dp)
    ) {
        PremiumTopBar("Settings & safety", onBack)
        Spacer(Modifier.height(18.dp))
        Text("ALARM READINESS", color = IradexColors.Text, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Text("All three permissions help Iradex reach you reliably.", color = IradexColors.Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
        PermissionRow(Icons.Default.NotificationsActive, "Notifications", "Show the commitment alarm", notificationsReady) {
            if (!notificationsReady) onRequestNotifications()
        }
        PermissionRow(Icons.Default.Schedule, "Exact alarm", "Ring at the time you choose", exactReady) {
            if (!exactReady && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                })
            }
        }
        PermissionRow(Icons.Default.Alarm, "Full-screen alert", "Open over the lock screen", fullScreenReady) {
            if (!fullScreenReady && Build.VERSION.SDK_INT >= 34) {
                context.startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                    data = Uri.parse("package:${context.packageName}")
                })
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onTestAlarm,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Primary)
        ) {
            Icon(Icons.Default.Alarm, null)
            Spacer(Modifier.width(8.dp))
            Text("Test alarm now", fontWeight = FontWeight.Bold)
        }
        Text("The test starts immediately. Use Emergency stop to end it.", color = IradexColors.Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 9.dp))

        Spacer(Modifier.height(28.dp))
        Text("PRIVACY & SAFETY", color = IradexColors.Text, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(12.dp))
        InformationCard(Icons.Default.Lock, "Private by default", "Commitments, history and proof remain locally on this phone. This prototype has no account or cloud upload.")
        Spacer(Modifier.height(10.dp))
        InformationCard(Icons.Default.HealthAndSafety, "Safety before streaks", "Every alarm includes Emergency stop. Missing a task should never trap, shame or endanger you.")

        if (hasCommitment) {
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { showCancelConfirmation = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(19.dp),
                border = BorderStroke(1.dp, IradexColors.Danger),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = IradexColors.Danger)
            ) {
                Icon(Icons.Default.DeleteOutline, null)
                Spacer(Modifier.width(8.dp))
                Text("Cancel active commitment", fontWeight = FontWeight.SemiBold)
            }
        }

        if (showCancelConfirmation) {
            AlertDialog(
                onDismissRequest = { showCancelConfirmation = false },
                containerColor = Color(0xFF191B22),
                titleContentColor = IradexColors.Text,
                textContentColor = IradexColors.Muted,
                title = { Text("Cancel this commitment?") },
                text = { Text("The scheduled alarm will be removed. Your completed progress history will stay on this phone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelConfirmation = false
                            onCancel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Danger)
                    ) { Text("Cancel commitment") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showCancelConfirmation = false }) { Text("Keep it") }
                }
            )
        }

        Spacer(Modifier.height(28.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Color(0xFF12141A), border = BorderStroke(1.dp, Color(0xFF262932))) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = IradexColors.PrimarySoft)
                Column(Modifier.padding(start = 13.dp)) {
                    Text("Iradex alpha 0.3.0", color = IradexColors.Text, fontWeight = FontWeight.SemiBold)
                    Text("Built for private prototype testing · Pakistan", color = IradexColors.Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}

@Composable
private fun MainTabHeader(title: String, eyebrow: String) {
    Column(Modifier.fillMaxWidth().padding(top = 22.dp)) {
        Text(eyebrow, color = IradexColors.PrimarySoft, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
        Text(title, color = IradexColors.Text, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun PermissionRow(icon: ImageVector, title: String, copy: String, ready: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14161C),
        border = BorderStroke(1.dp, Color(0xFF292C34))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = RoundedCornerShape(14.dp), color = if (ready) Color(0xFF17342A) else Color(0xFF28213E)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = if (ready) IradexColors.Success else IradexColors.PrimarySoft) }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(title, color = IradexColors.Text, fontWeight = FontWeight.SemiBold)
                Text(copy, color = IradexColors.Muted, fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(12.dp), color = if (ready) Color(0xFF17342A) else Color(0xFF28213E)) {
                Row(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (ready) "READY" else "SET UP", color = if (ready) IradexColors.Success else IradexColors.PrimarySoft, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    if (!ready) Icon(Icons.Default.OpenInNew, null, tint = IradexColors.PrimarySoft, modifier = Modifier.padding(start = 3.dp).size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun InformationCard(icon: ImageVector, title: String, copy: String) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color(0xFF14161C), border = BorderStroke(1.dp, Color(0xFF292C34))) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = IradexColors.PrimarySoft, modifier = Modifier.size(24.dp))
            Column(Modifier.padding(start = 13.dp)) {
                Text(title, color = IradexColors.Text, fontWeight = FontWeight.SemiBold)
                Text(copy, color = IradexColors.Muted, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}

@Composable
private fun MiniMetric(icon: ImageVector, value: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Text(value, color = IradexColors.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        Text(label, color = IradexColors.Muted, fontSize = 9.sp)
    }
}

@Composable
fun PremiumTopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(Modifier.size(42.dp), shape = CircleShape, color = Color(0xFF171920), border = BorderStroke(1.dp, FlowBorder)) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = IradexColors.Text) }
        }
        Text(title, color = IradexColors.Text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 14.dp))
    }
}
