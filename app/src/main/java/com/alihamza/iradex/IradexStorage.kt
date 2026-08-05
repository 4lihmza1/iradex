package com.alihamza.iradex

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object IradexStorage {
    private const val PREFS = "iradex_private"
    private const val ACTIVE = "active_commitment"
    private const val HISTORY = "history"
    private const val ONBOARDED = "onboarded"
    private const val WAITING_FOR_EXACT_ALARM = "waiting_for_exact_alarm"
    private const val ONBOARDING_FRICTION = "onboarding_friction"
    private const val ONBOARDING_GOAL = "onboarding_goal"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isOnboarded(context: Context) = prefs(context).getBoolean(ONBOARDED, false)
    fun setOnboarded(context: Context) = prefs(context).edit().putBoolean(ONBOARDED, true).apply()

    fun saveOnboardingProfile(context: Context, friction: String, goal: String) =
        prefs(context).edit()
            .putString(ONBOARDING_FRICTION, friction)
            .putString(ONBOARDING_GOAL, goal)
            .putBoolean(ONBOARDED, true)
            .apply()

    fun onboardingGoal(context: Context) =
        prefs(context).getString(ONBOARDING_GOAL, "Learning") ?: "Learning"

    fun saveCommitment(context: Context, item: Commitment) {
        val json = JSONObject()
            .put("id", item.id)
            .put("task", item.task)
            .put("category", item.category)
            .put("hour", item.alarmHour)
            .put("minute", item.alarmMinute)
            .put("proof", item.proofMethod)
        prefs(context).edit().putString(ACTIVE, json.toString()).apply()
    }

    fun loadCommitment(context: Context): Commitment? = runCatching {
        val raw = prefs(context).getString(ACTIVE, null) ?: return null
        val json = JSONObject(raw)
        Commitment(
            id = json.getLong("id"),
            task = json.getString("task"),
            category = json.getString("category"),
            alarmHour = json.getInt("hour"),
            alarmMinute = json.getInt("minute"),
            proofMethod = json.optString("proof", "Photo of progress")
        )
    }.getOrNull()

    fun clearCommitment(context: Context) = prefs(context).edit().remove(ACTIVE).apply()

    fun isWaitingForExactAlarmPermission(context: Context) =
        prefs(context).getBoolean(WAITING_FOR_EXACT_ALARM, false)

    fun setWaitingForExactAlarmPermission(context: Context, waiting: Boolean) =
        prefs(context).edit().putBoolean(WAITING_FOR_EXACT_ALARM, waiting).apply()

    fun completeCommitment(context: Context, partial: Boolean = false) {
        val active = loadCommitment(context) ?: return
        val history = JSONArray(prefs(context).getString(HISTORY, "[]"))
        history.put(
            JSONObject()
                .put("task", active.task)
                .put("category", active.category)
                .put("completedAt", System.currentTimeMillis())
                .put("partial", partial)
                .put("proof", active.proofMethod)
        )
        prefs(context).edit()
            .putString(HISTORY, history.toString())
            .remove(ACTIVE)
            .apply()
    }

    fun history(context: Context): List<HistoryItem> = runCatching {
        val array = JSONArray(prefs(context).getString(HISTORY, "[]"))
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            HistoryItem(
                task = item.getString("task"),
                category = item.getString("category"),
                completedAt = item.getLong("completedAt"),
                partial = item.optBoolean("partial", false),
                proofMethod = item.optString("proof", "Photo of progress")
            )
        }.reversed()
    }.getOrDefault(emptyList())

    fun completionRate(context: Context): Int {
        val items = history(context)
        if (items.isEmpty()) return 0
        val full = items.count { !it.partial }
        return ((full.toDouble() / items.size) * 100).toInt()
    }

    fun currentStreak(context: Context): Int {
        val completedDays = history(context)
            .filter { !it.partial }
            .map {
                java.util.Calendar.getInstance().apply { timeInMillis = it.completedAt }.run {
                    Triple(get(java.util.Calendar.YEAR), get(java.util.Calendar.DAY_OF_YEAR), timeInMillis)
                }
            }
            .distinctBy { it.first to it.second }
            .sortedByDescending { it.third }
        if (completedDays.isEmpty()) return 0
        var streak = 0
        val cursor = java.util.Calendar.getInstance()
        repeat(completedDays.size + 1) {
            val match = completedDays.any {
                it.first == cursor.get(java.util.Calendar.YEAR) &&
                    it.second == cursor.get(java.util.Calendar.DAY_OF_YEAR)
            }
            if (match) streak++ else if (streak > 0 || it > 0) return streak
            cursor.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }
}
