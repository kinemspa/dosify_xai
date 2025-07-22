package com.xai.core.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xai.dosify.R  // App's R for ic_notification

class YourAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("scheduleId", -1)

        val notification = NotificationCompat.Builder(context, "dose_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Dose Reminder")
            .setContentText("Time for your medication dose!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        manager.notify(scheduleId.toInt(), notification)
    }
}