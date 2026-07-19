package com.alihamza.iradex

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : ComponentActivity() {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        active = this
        startSignal()
        val task = intent.getStringExtra("task") ?: "Your commitment is ready"
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.radialGradient(listOf(Color(0xFFE64F5D), Color(0xFF9E2435), Color(0xFF26101A)))
                    ).padding(24.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("COMMITMENT ALARM", fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(32.dp))
                            Text(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()), fontSize = 70.sp, fontWeight = FontWeight.Black)
                            Text(SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()), color = Color(0xFFFFD6D5))
                            Spacer(Modifier.height(34.dp))
                            Text(task, fontSize = 29.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(10.dp))
                            Text("Show one small piece of real progress to complete this commitment.", color = Color(0xFFFFD2D1), textAlign = TextAlign.Center)
                            Spacer(Modifier.height(30.dp))
                            Surface(Modifier.size(138.dp), shape = CircleShape, color = Color.White) {
                                Box(contentAlignment = Alignment.Center) { Text("!", color = Color(0xFFA21F31), fontSize = 54.sp, fontWeight = FontWeight.Black) }
                            }
                        }
                        Column(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    startActivity(Intent(this@AlarmActivity, MainActivity::class.java).apply {
                                        putExtra("open", "proof")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    })
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF941C2B))
                            ) { Text("Submit proof to stop alarm", fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { stopAndFinish() },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) { Text("Emergency stop", fontWeight = FontWeight.Bold) }
                            Text("Safety always comes before completion.", Modifier.fillMaxWidth().padding(top = 10.dp), color = Color(0xFFFFBBB8), textAlign = TextAlign.Center, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    private fun startSignal() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        player = MediaPlayer().apply {
            setDataSource(this@AlarmActivity, uri)
            setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
            isLooping = true
            prepare()
            start()
        }
        vibrator = if (android.os.Build.VERSION.SDK_INT >= 31) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else @Suppress("DEPRECATION") (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 400, 800), 0))
    }

    private fun stopAndFinish() {
        player?.stop(); player?.release(); player = null
        vibrator?.cancel()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(AlarmReceiver.NOTIFICATION_ID)
        if (!isFinishing) finish()
    }

    override fun onDestroy() {
        if (active === this) active = null
        if (isFinishing) { player?.release(); vibrator?.cancel() }
        super.onDestroy()
    }

    companion object {
        private var active: AlarmActivity? = null
        fun stopActiveAlarm() { active?.runOnUiThread { active?.stopAndFinish() } }
    }
}
