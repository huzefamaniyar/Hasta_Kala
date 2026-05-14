package com.example.hasta_kala.ui.components

import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun DashboardGauge(
    percentage: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setHoleColor(AndroidColor.TRANSPARENT)
                setTransparentCircleAlpha(0)
                holeRadius = 85f
                setDrawCenterText(true)
                setCenterTextColor(AndroidColor.WHITE)
                setCenterTextSize(14f)
                setTouchEnabled(false)
                setExtraOffsets(0f, 0f, 0f, 0f)
                minOffset = 0f
            }
        },
        update = { chart ->
            chart.centerText = "${percentage.toInt()}%"
            val entries = listOf(
                PieEntry(percentage),
                PieEntry(100f - percentage)
            )
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    AndroidColor.parseColor("#F95E14"),
                    AndroidColor.parseColor("#1F0F09")
                )
                setDrawValues(false)
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun BestSellersChart(
    entries: List<PieEntry>,
    modifier: Modifier = Modifier
) {
    val totalQty = entries.sumOf { it.value.toDouble() }.toInt()
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.apply {
                    isEnabled = true
                    textColor = AndroidColor.LTGRAY
                    verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                    orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    xEntrySpace = 12f
                    textSize = 10f
                }
                setHoleColor(AndroidColor.parseColor("#2D1B14"))
                holeRadius = 60f
                setDrawCenterText(true)
                setCenterTextColor(AndroidColor.WHITE)
                setCenterTextSize(12f)
            }
        },
        update = { chart ->
            chart.centerText = "Total\n$totalQty"
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    AndroidColor.parseColor("#F95E14"),
                    AndroidColor.parseColor("#AC8981"),
                    AndroidColor.parseColor("#4D4939"),
                    AndroidColor.parseColor("#802A00"),
                    AndroidColor.parseColor("#5A4138")
                )
                setDrawValues(false)
                sliceSpace = 3f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun TrendingBarChart(
    entries: List<BarEntry>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = AndroidColor.LTGRAY
                    granularity = 1f
                }
                
                axisLeft.apply {
                    textColor = AndroidColor.LTGRAY
                    setDrawGridLines(true)
                    gridColor = AndroidColor.parseColor("#453028")
                }
                
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels.getOrNull(value.toInt()) ?: ""
                }
            }
            val dataSet = BarDataSet(entries, "").apply {
                color = AndroidColor.parseColor("#F95E14")
                setDrawValues(false)
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }
            chart.invalidate()
        },
        modifier = modifier
    )
}
