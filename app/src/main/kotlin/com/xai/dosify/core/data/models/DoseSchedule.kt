package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.xai.dosify.core.data.Converters
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "dose_schedules")
@TypeConverters(Converters::class)
data class DoseSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medId: Long,
    val doseAmount: Double,
    val unit: String,
    val frequency: Frequency,
    val times: List<LocalTime>,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val cycleWeeks: Int? = null,
    val cycleOffWeeks: Int? = null,
    val isCycling: Boolean = false,
    val titrationSteps: List<TitrationStep>? = null
)

data class TitrationStep(
    val period: Int,
    val doseAmount: Double
)

enum class Frequency {
    DAILY, WEEKLY, CUSTOM
}