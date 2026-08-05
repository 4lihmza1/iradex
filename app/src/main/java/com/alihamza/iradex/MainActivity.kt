package com.alihamza.iradex

import android.Manifest
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

private val Navy = Color(0xFF0D142B)
private val Card = Color(0xFF171F3A)
private val Violet = Color(0xFF8067FF)
private val VioletSoft = Color(0xFFB6A9FF)
private val Muted = Color(0xFFAEB6D0)
private val Green = Color(0xFF39D98A)
private val Coral = Color(0xFFFF6474)

private enum class Screen { Welcome, Home, Create, Proof, Success, History, Settings }

class MainActivity : ComponentActivity() {
    private var appScreen by mutableStateOf(Screen.Home)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appScreen = when {
            intent.getStringExtra("open") == "proof" -> Screen.Proof
            !IradexStorage.isOnboarded(this) -> Screen.Welcome
            else -> Screen.Home
        }
        setContent { IradexApp(appScreen) { appScreen = it } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getStringExtra("open") == "proof") appScreen = Screen.Proof
    }

    override fun onResume() {
        super.onResume()
        if (IradexStorage.isWaitingForExactAlarmPermission(this) && AlarmScheduler.canSchedule(this)) {
            IradexStorage.loadCommitment(this)?.let { AlarmScheduler.schedule(this, it) }
            IradexStorage.setWaitingForExactAlarmPermission(this, false)
            requestFullScreenAlarmPermission(this)
        }
    }
}

@Composable
private fun IradexApp(current: Screen, navigate: (Screen) -> Unit) {
    val context = LocalContext.current
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    IradexTheme {
        Surface(Modifier.fillMaxSize(), color = Navy) {
            when (current) {
                Screen.Welcome -> IradexOnboarding { friction, goal ->
                    IradexStorage.saveOnboardingProfile(context, friction, goal)
                    navigate(Screen.Create)
                }
                Screen.Home -> PremiumHomeScreen(
                    commitment = IradexStorage.loadCommitment(context),
                    onCreate = { navigate(Screen.Create) },
                    onProof = { navigate(Screen.Proof) },
                    onHistory = { navigate(Screen.History) },
                    onSettings = { navigate(Screen.Settings) }
                )
                Screen.Create -> CreateScreen(
                    onBack = { navigate(Screen.Home) },
                    onSave = { commitment ->
                        IradexStorage.saveCommitment(context, commitment)
                        if (AlarmScheduler.canSchedule(context)) {
                            AlarmScheduler.schedule(context, commitment)
                            requestFullScreenAlarmPermission(context)
                            navigate(Screen.Home)
                        } else {
                            IradexStorage.setWaitingForExactAlarmPermission(context, true)
                            requestExactAlarmPermission(context)
                            navigate(Screen.Home)
                        }
                    }
                )
                Screen.Proof -> ProofScreen(
                    commitment = IradexStorage.loadCommitment(context),
                    onBack = { navigate(Screen.Home) },
                    onComplete = {
                        IradexStorage.completeCommitment(context)
                        AlarmScheduler.cancel(context)
                        AlarmActivity.stopActiveAlarm()
                        AlarmSignalService.stop(context)
                        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                            .cancel(AlarmReceiver.NOTIFICATION_ID)
                        navigate(Screen.Success)
                    }
                )
                Screen.Success -> SuccessScreen { navigate(Screen.Home) }
                Screen.History -> HistoryScreen(IradexStorage.history(context)) { navigate(Screen.Home) }
                Screen.Settings -> SettingsScreen(
                    hasCommitment = IradexStorage.loadCommitment(context) != null,
                    onBack = { navigate(Screen.Home) },
                    onCancel = {
                        AlarmScheduler.cancel(context)
                        IradexStorage.clearCommitment(context)
                        navigate(Screen.Home)
                    }
                )
            }
        }
    }
}

private fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        runCatching {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            })
        }
    }
}

private fun requestFullScreenAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= 34) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!manager.canUseFullScreenIntent()) {
            runCatching {
                context.startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                    data = Uri.parse("package:${context.packageName}")
                })
            }
        }
    }
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF17133A), Navy, Color(0xFF090E20)))
        ).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(10.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(Modifier.size(92.dp), shape = RoundedCornerShape(28.dp), color = Violet) {
                Box(contentAlignment = Alignment.Center) { Text("I", fontSize = 52.sp, fontWeight = FontWeight.Black) }
            }
            Spacer(Modifier.height(30.dp))
            Text("IRADEX", fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = 5.sp)
            Spacer(Modifier.height(12.dp))
            Text("Turn intention into action.", color = VioletSoft, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(22.dp))
            Text(
                "Choose one meaningful task. When the time arrives, prove a small piece of real progress.",
                color = Muted, textAlign = TextAlign.Center, lineHeight = 24.sp
            )
        }
        Column(Modifier.fillMaxWidth()) {
            PrimaryButton("Create my first commitment", Icons.Default.ArrowForward, onStart)
            Text(
                "You can always use Emergency stop. Safety comes first.",
                Modifier.fillMaxWidth().padding(top = 14.dp), color = Muted, textAlign = TextAlign.Center, fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HomeScreen(
    commitment: Commitment?, onCreate: () -> Unit, onProof: () -> Unit,
    onHistory: () -> Unit, onSettings: () -> Unit
) {
    val context = LocalContext.current
    val streak = IradexStorage.currentStreak(context)
    val completionRate = IradexStorage.completionRate(context)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("IRADEX", fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = VioletSoft)
                Text("One commitment at a time", color = Muted, fontSize = 13.sp)
            }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings", tint = Muted) }
        }
        Spacer(Modifier.height(36.dp))
        Text(if (commitment == null) "Ready when you are." else "Your active commitment", fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(
            if (commitment == null) "Pick one useful thing. Make the next step small enough to begin."
            else "Stay with this one task until you have made visible progress.",
            color = Muted, lineHeight = 22.sp
        )
        Spacer(Modifier.height(26.dp))
        if (commitment == null) {
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = Card) {
                Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(Modifier.size(70.dp), shape = CircleShape, color = Color(0xFF282252)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AddAlarm, null, tint = VioletSoft, modifier = Modifier.size(34.dp)) }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("No active commitment", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Create one commitment and choose when Iradex should call you to action.", color = Muted, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(22.dp))
                    PrimaryButton("Create commitment", Icons.Default.Add, onCreate)
                }
            }
        } else {
            CommitmentCard(commitment, onProof)
        }
        Spacer(Modifier.height(22.dp))
        Text("MOMENTUM", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(Modifier.weight(1f), shape = RoundedCornerShape(20.dp), color = Card) {
                Column(Modifier.padding(18.dp)) {
                    Text(streak.toString(), color = Color(0xFFF7C967), fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text("day streak", color = Muted, fontSize = 12.sp)
                }
            }
            Surface(Modifier.weight(1f), shape = RoundedCornerShape(20.dp), color = Card) {
                Column(Modifier.padding(18.dp)) {
                    Text("$completionRate%", color = Green, fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text("follow-through", color = Muted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        OutlinedButton(onClick = onHistory, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp)) {
            Icon(Icons.Default.History, null); Spacer(Modifier.width(9.dp)); Text("View progress history")
        }
        Spacer(Modifier.height(18.dp))
        Text("HOW IT WORKS", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))
        listOf(
            "1" to "Commit to one clear, achievable task",
            "2" to "The alarm starts at your chosen time",
            "3" to "Capture fresh proof of progress to complete it"
        ).forEach { (number, copy) ->
            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(34.dp), shape = CircleShape, color = Color(0xFF282252)) {
                    Box(contentAlignment = Alignment.Center) { Text(number, color = VioletSoft, fontWeight = FontWeight.Bold) }
                }
                Text(copy, Modifier.padding(start = 12.dp), color = Muted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun CommitmentCard(item: Commitment, onProof: () -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = Card) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF2B255B)) {
                    Text(item.category, Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = VioletSoft, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.NotificationsActive, null, tint = Coral)
            }
            Spacer(Modifier.height(20.dp))
            Text(item.task, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, tint = VioletSoft)
                Text(String.format(Locale.getDefault(), "%02d:%02d", item.alarmHour, item.alarmMinute), Modifier.padding(start = 9.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.proofMethod, color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(22.dp))
            PrimaryButton("Submit progress now", Icons.Default.CameraAlt, onProof)
        }
    }
}

@Composable
private fun CreateScreen(onBack: () -> Unit, onSave: (Commitment) -> Unit) {
    val context = LocalContext.current
    var task by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(IradexStorage.onboardingGoal(context)) }
    var hour by remember { mutableIntStateOf(18) }
    var minute by remember { mutableIntStateOf(0) }
    var proofMethod by remember { mutableStateOf("Photo of progress") }
    val categories = listOf("Learning", "Work", "Health", "Personal")

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("New commitment", onBack)
        Spacer(Modifier.height(24.dp))
        Text("What will you move forward?", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("Make it specific and small enough to prove.", color = Muted, modifier = Modifier.padding(top = 7.dp, bottom = 24.dp))
        Text("TASK", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = task, onValueChange = { if (it.length <= 100) task = it },
            placeholder = { Text("Example: Write 10 lines of Python") },
            modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(18.dp)
        )
        Text("${task.length}/100", Modifier.fillMaxWidth().padding(top = 5.dp), color = Muted, textAlign = TextAlign.End, fontSize = 11.sp)
        Spacer(Modifier.height(18.dp))
        Text("CATEGORY", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            categories.forEach { value ->
                FilterChip(
                    selected = category == value, onClick = { category = value },
                    label = { Text(value, fontSize = 11.sp) }, modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("ALARM TIME", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(9.dp))
        Surface(
            onClick = {
                TimePickerDialog(context, { _, h, m -> hour = h; minute = m }, hour, minute, false).show()
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Card
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Alarm, null, tint = VioletSoft)
                Text(String.format(Locale.getDefault(), "%02d:%02d", hour, minute), Modifier.padding(start = 14.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f)); Text("Change", color = VioletSoft)
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("PROOF METHOD", color = VioletSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(9.dp))
        listOf(
            "Photo of progress" to "Best for visible work or handwritten code",
            "Quick progress note" to "A short description of what changed"
        ).forEach { (method, description) ->
            Surface(
                onClick = { proofMethod = method },
                modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
                shape = RoundedCornerShape(18.dp),
                color = if (proofMethod == method) Color(0xFF1D1941) else Card,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (proofMethod == method) Violet else Color(0xFF27334D))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(if (method.startsWith("Photo")) Icons.Default.CameraAlt else Icons.Default.EditNote, null, tint = VioletSoft)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(method, fontWeight = FontWeight.Bold)
                        Text(description, color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { onSave(Commitment(task = task.trim(), category = category, alarmHour = hour, alarmMinute = minute, proofMethod = proofMethod)) },
            enabled = task.trim().length >= 3,
            modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp)
        ) { Text("Set commitment", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun ProofScreen(commitment: Commitment?, onBack: () -> Unit, onComplete: () -> Unit) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var captured by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> captured = success }
    fun launchCamera() {
        val directory = File(context.cacheDir, "proofs").apply { mkdirs() }
        val file = File(directory, "proof_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        photoUri = uri
        camera.launch(uri)
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera()
    }
    val noteOnly = commitment?.proofMethod == "Quick progress note"

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Submit proof", onBack)
        Spacer(Modifier.height(24.dp))
        Text("Show your progress", fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text(commitment?.task ?: "Capture something you worked on.", color = Muted, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
        if (!noteOnly) {
            Surface(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) launchCamera()
                    else cameraPermission.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth().height(235.dp), shape = RoundedCornerShape(26.dp),
                color = if (captured) Color(0xFF153A31) else Card
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(Modifier.size(70.dp), shape = CircleShape, color = if (captured) Green else Violet) {
                        Box(contentAlignment = Alignment.Center) { Icon(if (captured) Icons.Default.Check else Icons.Default.CameraAlt, null, modifier = Modifier.size(34.dp)) }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(if (captured) "Fresh proof captured" else "Open camera", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(if (captured) "Tap to retake" else "Take a photo now—gallery uploads are disabled", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        OutlinedTextField(
            value = note, onValueChange = { if (it.length <= 120) note = it },
            label = { Text(if (noteOnly) "Progress note" else "Optional note") },
            placeholder = { Text("What moved forward?") },
            modifier = Modifier.fillMaxWidth(), minLines = if (noteOnly) 4 else 1, shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(26.dp))
        Button(
            onClick = onComplete, enabled = if (noteOnly) note.trim().length >= 3 else captured,
            modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Navy)
        ) { Icon(Icons.Default.Verified, null); Spacer(Modifier.width(8.dp)); Text("Use this proof", fontWeight = FontWeight.Black) }
        Text(
            if (noteOnly) "Progress beats perfection. Describe the honest step you took."
            else "The photo is checked for fresh camera capture, not code correctness.",
            Modifier.padding(top = 13.dp), color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SuccessScreen(onDone: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF123D32), Navy))).padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Surface(Modifier.size(120.dp), shape = CircleShape, color = Green) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, null, tint = Navy, modifier = Modifier.size(62.dp)) }
        }
        Spacer(Modifier.height(30.dp))
        Text("Progress made.", fontSize = 34.sp, fontWeight = FontWeight.Black)
        Text("You kept your commitment and created real evidence of action.", Modifier.padding(top = 12.dp), color = Muted, textAlign = TextAlign.Center, lineHeight = 22.sp)
        Spacer(Modifier.height(34.dp))
        PrimaryButton("Back to home", Icons.Default.Home, onDone)
    }
}

@Composable
private fun HistoryScreen(items: List<HistoryItem>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Progress history", onBack)
        Spacer(Modifier.height(28.dp))
        if (items.isEmpty()) {
            Column(Modifier.fillMaxWidth().padding(vertical = 80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AutoGraph, null, tint = VioletSoft, modifier = Modifier.size(54.dp))
                Text("Your progress will appear here", Modifier.padding(top = 18.dp), fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("Complete your first commitment to begin.", color = Muted, modifier = Modifier.padding(top = 7.dp))
            }
        } else items.forEach { item ->
            Surface(Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(20.dp), color = Card) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(44.dp), shape = CircleShape, color = if (item.partial) Color(0xFF49391D) else Color(0xFF164133)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(if (item.partial) Icons.Default.HourglassBottom else Icons.Default.Check, null, tint = if (item.partial) Color(0xFFF7C967) else Green)
                        }
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                        Text(item.task, fontWeight = FontWeight.Bold)
                        Text("${item.category} • ${item.proofMethod} • ${SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(item.completedAt))}", color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(hasCommitment: Boolean, onBack: () -> Unit, onCancel: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        TopBar("Settings & safety", onBack)
        Spacer(Modifier.height(26.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Card) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HealthAndSafety, null, tint = Green)
                    Text("Safety by design", Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text("Every alarm includes an Emergency stop. Iradex should encourage action—never trap or endanger you.", Modifier.padding(top = 12.dp), color = Muted, lineHeight = 21.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Card) {
            Column(Modifier.padding(20.dp)) {
                Text("Privacy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Commitments, history, and proof photos remain locally on your phone in this prototype.", Modifier.padding(top = 8.dp), color = Muted)
            }
        }
        if (hasCommitment) {
            Spacer(Modifier.height(26.dp))
            OutlinedButton(
                onClick = onCancel, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral)
            ) { Icon(Icons.Default.DeleteOutline, null); Spacer(Modifier.width(8.dp)); Text("Cancel active commitment") }
        }
        Text("Iradex alpha 0.1.0", Modifier.fillMaxWidth().padding(top = 32.dp), color = Muted, textAlign = TextAlign.Center, fontSize = 12.sp)
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
        Text(title, Modifier.padding(start = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PrimaryButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp),
        shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet)
    ) { Text(label, fontWeight = FontWeight.Bold); Spacer(Modifier.width(9.dp)); Icon(icon, null) }
}
