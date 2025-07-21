package com.xai.dosify.feature.advanced.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.xai.dosify.feature.advanced.viewmodel.ReportsViewModel

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val logs by viewModel.doseLogs.collectAsState(emptyList())

    AndroidView(
        factory = { LineChart(it) },
        modifier = Modifier.fillMaxSize(),
        update = { chart ->
            val entries = logs.mapIndexed { i, log -> Entry(i.toFloat(), log.amountTaken.toFloat()) }
            val dataSet = LineDataSet(entries, "Dose History")
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}