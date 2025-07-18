package com.xai.dosify.core.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xai.dosify.core.data.models.TitrationStep

class Converters {
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalTimeList(times: List<LocalTime>): String = times.joinToString(",") { it.format(timeFormatter) }

    @TypeConverter
    fun toLocalTimeList(timesStr: String): List<LocalTime> = timesStr.split(",").map { LocalTime.parse(it, timeFormatter) }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.format(dateFormatter)

    @TypeConverter
    fun toLocalDate(dateStr: String): LocalDate = LocalDate.parse(dateStr, dateFormatter)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)

    @TypeConverter
    fun toLocalDateTime(dateTimeStr: String): LocalDateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter)

    private val gson = Gson()

    @TypeConverter
    fun fromTitrationSteps(steps: List<TitrationStep>?): String? = gson.toJson(steps)

    @TypeConverter
    fun toTitrationSteps(stepsStr: String?): List<TitrationStep>? {
        if (stepsStr == null) return null
        val type = object : TypeToken<List<TitrationStep>>() {}.type
        return gson.fromJson(stepsStr, type)
    }
}