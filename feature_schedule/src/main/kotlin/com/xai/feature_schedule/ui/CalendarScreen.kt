package com.xai.feature_schedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.xai.feature_schedule.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import com.xai.core.data.models.DoseSchedule


@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val schedules by viewModel.schedules.collectAsState(emptyList<DoseSchedule>())
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(12)
    val endMonth = currentMonth.plusMonths(12)
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    HorizontalCalendar(
        state = state,
        dayContent = { day: CalendarDay ->
            val hasDose = hasDoseOnDay(day.date, schedules)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(day.date.dayOfMonth.toString())
                if (hasDose) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    )
}

private fun hasDoseOnDay(day: LocalDate, schedules: List<DoseSchedule>): Boolean {
    return schedules.any { schedule ->
        day >= schedule.startDate && (schedule.endDate == null || day <= schedule.endDate)
        // Add frequency logic later, e.g., for WEEKLY: day.dayOfWeek in schedule.daysOfWeek
    }
}