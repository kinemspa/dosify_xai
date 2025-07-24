package com.xai.core.utils

import com.xai.core.data.models.DoseSchedule
import java.time.Duration
import java.time.LocalDateTime
import timber.log.Timber

fun calculateTimeLeftToNextDose(schedule: DoseSchedule): Duration {
    val next = nextTime(schedule) ?: return Duration.ZERO // Fallback
    Timber.d("Calculating time left: From now to $next")
    return Duration.between(LocalDateTime.now(), next)
}

// Stub for nextTime - implement based on frequency/times
private fun nextTime(schedule: DoseSchedule): LocalDateTime? {
    if (schedule.times.isEmpty()) return null
    val now = LocalDateTime.now()
    val nextTime = schedule.times.firstOrNull { it.isAfter(now.toLocalTime()) } ?: schedule.times.first() // Next today or tomorrow
    return if (nextTime.isAfter(now.toLocalTime())) {
        now.with(nextTime.hour, nextTime.minute, nextTime.second)
    } else {
        now.plusDays(1).with(nextTime.hour, nextTime.minute, nextTime.second)
    }
}

// Add other stats functions here, e.g., amountTakenOverTime