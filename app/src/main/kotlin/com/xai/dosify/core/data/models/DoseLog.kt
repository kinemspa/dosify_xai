package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "dose_logs")
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleId: Long,
    val takenTime: LocalDateTime,
    val amountTaken: Double,
    val notes: String? = null,
    val reaction: String? = null
)