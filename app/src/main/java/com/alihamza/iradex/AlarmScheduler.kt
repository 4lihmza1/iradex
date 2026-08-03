package com.alihamza.iradex

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    fun canSchedule(context: Context): Boolean {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()
    }

    fun schedule(context: Context, commitment: Commitment): Long? {
        if (!canSchedule(context)) return null
        val at = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, commitment.alarmHour)
            set(Calendar.MINUTE, commitment.alarmMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("task", commitment.task)
        }
        val pending = PendingIntent.getBroadcast(
            context, 4101, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val showPending = PendingIntent.getActivity(
            context,
            4102,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return runCatching {
            manager.setAlarmClock(AlarmManager.AlarmClockInfo(at, showPending), pending)
            at
        }.getOrNull()
    }

    fun cancel(context: Context) {
        val pending = PendingIntent.getBroadcast(
            context, 4101, Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending)
        pending.cancel()
    }
}
