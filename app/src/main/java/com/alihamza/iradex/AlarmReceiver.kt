package com.alihamza.iradex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getStringExtra("task") ?: "Your commitment is ready"
        val serviceIntent = Intent(context, AlarmSignalService::class.java).apply {
            putExtra("task", task)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    companion object {
        const val CHANNEL = "iradex_alarm_v2"
        const val NOTIFICATION_ID = 4202
    }
}
