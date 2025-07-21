package com.xai.dosify.core.com.xai.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.xai.dosify.core.com.xai.core.data.Converters
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDate
import java.time.LocalTime

@IgnoreExtraProperties
@Entity(tableName = "dose_schedules")
@TypeConverters(Converters::class)
data class DoseSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medId: Long = 0,
    val doseAmount: Double = 0.0,
    val unit: String = "",
    val frequency: Frequency = Frequency.DAILY,
    val times: List<LocalTime> = emptyList(),
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val isActive: Boolean = true,
    val cycleWeeks: Int? = null,
    val cycleOffWeeks: Int? = null,
    val isCycling: Boolean = false,
    val titrationSteps: List<TitrationStep>? = null
)

data class TitrationStep(
    val period: Int = 0,
    val doseAmount: Double = 0.0
)

enum class Frequency {
    DAILY, WEEKLY, CUSTOM
}