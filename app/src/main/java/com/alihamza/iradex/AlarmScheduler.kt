package com.alihamza.iradex

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    private const val RESTART_GRACE_MS = 15_000L

    fun canSchedule(context: Context): Boolean {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()
    }

    fun prepare(commitment: Commitment): Commitment =
        if (commitment.scheduledAt > 0L) commitment
        else commitment.copy(scheduledAt = nextOccurrence(commitment.alarmHour, commitment.alarmMinute))

    private fun nextOccurrence(hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }.timeInMillis

    private fun scheduleAt(context: Context, commitment: Commitment, at: Long): Long? {
        if (!canSchedule(context)) return null
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

    fun schedule(context: Context, commitment: Commitment): Long? {
        val prepared = prepare(commitment)
        val now = System.currentTimeMillis()
        val at = if (prepared.scheduledAt > now) prepared.scheduledAt else now + RESTART_GRACE_MS
        return scheduleAt(context, prepared, at)
    }

    fun scheduleAfterRestart(context: Context, commitment: Commitment): Long? {
        val now = System.currentTimeMillis()
        val savedAt = commitment.scheduledAt.takeIf { it > 0L }
            ?: Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, commitment.alarmHour)
                set(Calendar.MINUTE, commitment.alarmMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        val at = if (savedAt > now) savedAt else now + RESTART_GRACE_MS
        return scheduleAt(context, commitment, at)
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
