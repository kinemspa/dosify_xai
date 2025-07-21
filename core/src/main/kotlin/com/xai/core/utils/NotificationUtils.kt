package com.xai.core.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.xai.core.data.models.DoseSchedule
import com.xai.dosify.ui.notifications.*
import java.time.LocalTime
import java.time.Duration

fun setDoseAlarm(context: Context, schedule: DoseSchedule) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, YourAlarmReceiver::class.java).apply {
        putExtra("scheduleId", schedule.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        schedule.id.toInt(),
        intent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    )

    // Calculate trigger time (first time in schedule)
    val now = LocalTime.now()
    val firstTime = schedule.times.firstOrNull() ?: return
    val triggerTime = System.currentTimeMillis() + Duration.between(now, firstTime).toMillis()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        // Fallback: Use inexact alarm if permission denied
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    } else {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}