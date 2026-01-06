package uk.ac.tees.mad.s3540722.pennypinch.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp

@Composable
fun DashboardPieChart(
    income: Double,
    expenses: Double,
    investments: Double,
    onViewBudget: () -> Unit
) {
    val total = income + expenses + investments
    if (total <= 0) return

    val incomeColor = Color(0xFF71EAAD)
    val expenseColor = Color(0xFFFFE4E6)
    val investmentColor = Color(0xFFBEBF19)
    val budgetColor = Color(0xFF5B96F5)

    var showLegend by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /* ---------- HEADER ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Snapshot",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "☰",
                    modifier = Modifier
                        .clickable { showLegend = !showLegend }
                        .padding(4.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            /* ---------- PIE CHART ---------- */
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                var startAngle = -90f

                fun drawSlice(value: Double, color: Color) {
                    val sweep = (value / total * 360f).toFloat()
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweep
                }

                drawSlice(income, incomeColor)
                drawSlice(expenses, expenseColor)
                drawSlice(investments, investmentColor)
            }

            /* ---------- LEGEND ---------- */
            AnimatedVisibility(visible = showLegend) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem("Income", incomeColor)
                    LegendItem("Expenses", expenseColor)
                    LegendItem("Investments", investmentColor)
                    LegendItem("Budget", budgetColor)
                }
            }

            /* ---------- VIEW BUDGET ---------- */
            Text(
                text = "View Budget Insights →",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewBudget() }
            )
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}
