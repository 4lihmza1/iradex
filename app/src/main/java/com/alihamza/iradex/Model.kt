package com.alihamza.iradex

data class Commitment(
    val id: Long = System.currentTimeMillis(),
    val task: String,
    val category: String,
    val alarmHour: Int,
    val alarmMinute: Int,
    val proofMethod: String = "Photo of progress"
)

data class HistoryItem(
    val task: String,
    val category: String,
    val completedAt: Long,
    val partial: Boolean = false,
    val proofMethod: String = "Photo of progress"
)
