package com.alihamza.iradex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                IradexStorage.loadBootCommitment(context)
                    ?.let { AlarmScheduler.scheduleAfterRestart(context, it) }
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED" -> {
                IradexStorage.ensureBootCopy(context)
                (IradexStorage.loadCommitment(context) ?: IradexStorage.loadBootCommitment(context))
                    ?.let { AlarmScheduler.scheduleAfterRestart(context, it) }
            }
        }
    }
}
