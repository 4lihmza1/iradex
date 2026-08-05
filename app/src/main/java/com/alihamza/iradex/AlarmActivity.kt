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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.radialGradient(
                            0.0f to Color(0xFF5A1828),
                            0.42f to Color(0xFF24101A),
                            1.0f to Color(0xFF08090C)
                        )
                    ).padding(horizontal = 24.dp, vertical = 22.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0x33FF667A),
                                border = BorderStroke(1.dp, Color(0x66FF667A))
                            ) {
                                Row(Modifier.padding(horizontal = 13.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, null, tint = IradexColors.Danger, modifier = Modifier.size(17.dp))
                                    Text("COMMITMENT CALL", Modifier.padding(start = 7.dp), color = Color(0xFFFFA5AF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Text(
                                SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
                                color = IradexColors.Text,
                                fontSize = 68.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-2).sp
                            )
                            Text(
                                SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()),
                                color = Color(0xFFFFABB3),
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(26.dp))
                            Surface(
                                Modifier.size(150.dp),
                                shape = RoundedCornerShape(48.dp),
                                color = Color(0x331B1D25),
                                border = BorderStroke(1.dp, Color(0x66FF667A))
                            ) {
                                Box(
                                    Modifier.background(Brush.radialGradient(listOf(Color(0x55FF667A), Color.Transparent))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(painterResource(R.drawable.ic_iradex), null, modifier = Modifier.size(108.dp))
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(task, color = IradexColors.Text, fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(
                                "Make one honest step, then prove what moved forward.",
                                color = Color(0xFFD2B1B7),
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp,
                                modifier = Modifier.padding(top = 11.dp)
                            )
                        }

                        Column(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    startActivity(Intent(this@AlarmActivity, MainActivity::class.java).apply {
                                        putExtra("open", "proof")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    })
                                },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IradexColors.Text, contentColor = Color(0xFF2C0A11))
                            ) {
                                Text("Show progress to complete", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, null)
                            }
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { stopAndFinish() },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(19.dp),
                                border = BorderStroke(1.dp, Color(0x99FF8B96)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFA5AF))
                            ) {
                                Icon(Icons.Default.HealthAndSafety, null)
                                Spacer(Modifier.width(7.dp))
                                Text("Emergency stop", fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                "Safety always comes before completion or streaks.",
                                Modifier.fillMaxWidth().padding(top = 9.dp),
                                color = Color(0xFFAA7E85),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
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
