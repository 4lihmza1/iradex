package com.alihamza.iradex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getStringExtra("task") ?: "Your commitment is ready"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL, "Commitment alarms", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "User-scheduled Iradex alarms"
                enableVibration(true)
                setSound(sound, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
            }
        )
        val fullIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("task", task)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullPending = PendingIntent.getActivity(
            context, 4201, fullIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_iradex)
            .setContentTitle("Commitment alarm")
            .setContentText(task)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setFullScreenIntent(fullPending, true)
            .setContentIntent(fullPending)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL = "iradex_alarm"
        const val NOTIFICATION_ID = 4202
    }
}
