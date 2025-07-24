package com.xai.dosify.core.utils

import com.xai.dosify.core.data.models.DoseScheduleEntity
import java.time.Duration
import java.time.LocalDateTime
import timber.log.Timber

fun calculateTimeLeftToNextDose(schedule: DoseScheduleEntity): Duration {
    val next = nextTime(schedule) ?: return Duration.ZERO // Fallback
    Timber.d("Calculating time left: From now to $next")
    return Duration.between(LocalDateTime.now(), next)
}

// Other functions with similar null checks and logs