package com.alihamza.iradex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class AlarmSignalService : Service() {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val task = intent?.getStringExtra("task") ?: "Your commitment is ready"
        createChannel()

        val alarmIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("task", task)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val alarmPendingIntent = PendingIntent.getActivity(
            this,
            4201,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, AlarmReceiver.CHANNEL)
            .setSmallIcon(R.drawable.ic_iradex)
            .setContentTitle("Commitment alarm")
            .setContentText(task)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(alarmPendingIntent, true)
            .setContentIntent(alarmPendingIntent)
            .build()

        startForeground(AlarmReceiver.NOTIFICATION_ID, notification)
        startSignal()
        return START_NOT_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    AlarmReceiver.CHANNEL,
                    "Commitment alarms",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "User-scheduled Iradex alarms"
                    setSound(null, null)
                    enableVibration(false)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                }
            )
        }
    }

    private fun startSignal() {
        if (player?.isPlaying == true) return
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        player = MediaPlayer().apply {
            setDataSource(this@AlarmSignalService, alarmUri)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            isLooping = true
            prepare()
            start()
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 400, 800), 0))
    }

    override fun onDestroy() {
        runCatching { player?.stop() }
        player?.release()
        player = null
        vibrator?.cancel()
        vibrator = null
        super.onDestroy()
    }

    companion object {
        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmSignalService::class.java))
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(AlarmReceiver.NOTIFICATION_ID)
        }
    }
}

