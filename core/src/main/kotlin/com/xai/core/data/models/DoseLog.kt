package com.xai.dosify.core.com.xai.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDateTime

@IgnoreExtraProperties
@Entity(tableName = "dose_logs")
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleId: Long = 0,
    val takenTime: LocalDateTime = LocalDateTime.now(),
    val amountTaken: Double = 0.0,
    val notes: String? = null,
    val reaction: String? = null
)