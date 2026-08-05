package com.alihamza.iradex

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        active = this
        val task = intent.getStringExtra("task") ?: "Your commitment is ready"

        setContent {
            IradexTheme {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    Image(
                        painter = painterResource(R.drawable.iradex_alarm_core),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                0.0f to Color(0xAA050609),
                                0.18f to Color(0x22050609),
                                0.56f to Color.Transparent,
                                0.76f to Color(0xB0050609),
                                1.0f to Color(0xFF050609)
                            )
                        )
                    )
                    Column(
                        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xB31C111B),
                            border = BorderStroke(1.dp, Color(0x88FF6474))
                        ) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFFFF8290), modifier = Modifier.size(16.dp))
                                Text("YOUR MOMENT IS HERE", Modifier.padding(start = 7.dp), color = Color(0xFFFFA6AF), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
                            color = IradexColors.Text,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.5).sp
                        )
                        Text(
                            SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()),
                            color = Color(0xFFD9B7C1),
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.weight(1f))

                        Surface(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            color = Color(0xED14151B),
                            border = BorderStroke(1.dp, Color(0xFF3A3039)),
                            shadowElevation = 6.dp
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("YOUR COMMITMENT", color = Color(0xFFFF8A96), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                                Text(
                                    task,
                                    color = IradexColors.Text,
                                    fontSize = 24.sp,
                                    lineHeight = 29.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(
                                    "Make one honest step. Progress—not perfection—stops this alarm.",
                                    color = IradexColors.Muted,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(Modifier.height(18.dp))
                                Button(
                                    onClick = {
                                        startActivity(Intent(this@AlarmActivity, MainActivity::class.java).apply {
                                            putExtra("open", "proof")
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        })
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Text, contentColor = Color(0xFF2C0A11))
                                ) {
                                    Text("Show progress", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, null)
                                }
                                Spacer(Modifier.height(9.dp))
                                OutlinedButton(
                                    onClick = { stopAndFinish() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(17.dp),
                                    border = BorderStroke(1.dp, Color(0x77FF8290)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFA6AF))
                                ) {
                                    Icon(Icons.Default.HealthAndSafety, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(7.dp))
                                    Text("Emergency stop", fontWeight = FontWeight.SemiBold)
                                }
                                Text(
                                    "Safety always comes before completion or streaks.",
                                    Modifier.fillMaxWidth().padding(top = 8.dp),
                                    color = Color(0xFF8E777E),
                                    textAlign = TextAlign.Center,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun stopAndFinish() {
        AlarmSignalService.stop(this)
        if (!isFinishing) finish()
    }

    override fun onDestroy() {
        if (active === this) active = null
        super.onDestroy()
    }

    companion object {
        private var active: AlarmActivity? = null
        fun stopActiveAlarm() {
            active?.runOnUiThread { active?.stopAndFinish() }
        }
    }
}
