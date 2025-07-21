package com.xai.dosify.feature.schedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.YearMonth

@Composable
fun CalendarScreen() {
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(12)
    val endMonth = currentMonth.plusMonths(12)
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    WeekCalendar(
        modifier = Modifier.fillMaxSize(),
        state = state,
        dayContent = { day: CalendarDay ->
            Text(day.date.dayOfMonth.toString())  // Add dose dots later
        }
    )
}