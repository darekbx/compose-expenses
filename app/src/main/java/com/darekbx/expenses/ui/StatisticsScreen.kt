package com.darekbx.expenses.ui

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.StatisticValue
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun StatisticsScreen(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val statistics by expensesViewModel.loadStatistics().observeAsState()
    statistics?.let {
        Box() {
            PieChart(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                statistics = it
            )
        }
    }
}

@Composable
private fun PieChart(modifier: Modifier = Modifier, statistics: List<StatisticValue>) {
    if (statistics.isEmpty()) return

    Canvas(modifier = modifier) {
        val padding = 4F
        val canvasWidth = size.width - padding * 2
        val canvasHeight = size.height - padding * 2
        val chartArea = calculateChartRectange(canvasWidth, canvasHeight)

        val offset = Offset(chartArea.left + padding, chartArea.top + padding)
        val size = Size(chartArea.width(), chartArea.height())

        var angleStart = 0F
        val angles = mutableListOf<Float>()

        statistics.forEach {
            val color = it.type.color
            val arcTo = it.percent.toFloat() * 3.6F

            drawArc(color, angleStart, arcTo, true, offset, size)
            drawArc(Color.Black, angleStart, arcTo, true, offset, size, style = Stroke(4.0F))

            angleStart += arcTo
            angles.add(arcTo)
        }
    }
}

private fun calculateChartRectange(width: Float, height: Float): RectF {
    var xOffset = 0F
    var yOffset = 0F
    val size = when (width > height) {
        true -> {
            xOffset = (width - height) / 2F
            height
        }
        else -> {
            yOffset = (height - width) / 2F
            width
        }
    }
    return RectF(xOffset, yOffset, size + xOffset, size + yOffset)
}