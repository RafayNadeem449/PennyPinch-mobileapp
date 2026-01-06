package uk.ac.tees.mad.s3540722.pennypinch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.PlannedBudget

@Composable
fun BudgetInsightsScreen(nav: NavController) {

    val scope = rememberCoroutineScope()

    var budget by remember { mutableStateOf<PlannedBudget?>(null) }
    var actual by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    LaunchedEffect(Unit) {
        scope.launch {

            val plannedBudget = FirebaseService.getPlannedBudget()
            budget = plannedBudget

            if (plannedBudget == null) {
                actual = emptyMap()
                return@launch
            }

            val plannedCategories = plannedBudget.allocations.keys.toSet()

            actual = FirebaseService.getTransactions()
                .filter { it.type == "Expense" }
                .groupBy { tx ->
                    normalizeCategory(tx.category, plannedCategories)
                }
                .mapValues { (_, txs) ->
                    txs.sumOf { it.amount }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Budget Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Back",
                modifier = Modifier.clickable { nav.popBackStack() }
            )
        }

        if (budget == null) {
            Text("No budget found")
            return@Column
        }

        /* ---------- MERGE PLANNED + OTHER ---------- */
        val plannedAllocations = budget!!.allocations.toMutableMap()
        if (!plannedAllocations.containsKey("Other")) {
            plannedAllocations["Other"] = 0.0
        }

        /* ---------- BUDGET CARDS ---------- */
        plannedAllocations.forEach { (category, plannedAmount) ->

            val spentAmount = actual[category] ?: 0.0
            val exceeded = plannedAmount > 0 && spentAmount > plannedAmount

            val progress = if (plannedAmount > 0.0) {
                (spentAmount / plannedAmount).coerceIn(0.0, 1.0)
            } else {
                0.0
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {

                    Text(category, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "£${"%.2f".format(spentAmount)} / £${"%.2f".format(plannedAmount)}",
                        color = if (exceeded) Color.Red else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (exceeded) Color.Red else MaterialTheme.colorScheme.primary
                    )

                    /* ---------- EXCEEDED MESSAGE ---------- */
                    if (exceeded) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "⚠ You have exceeded your budget for $category by £${"%.2f".format(spentAmount - plannedAmount)}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/* ================================================= */
/* =================== HELPERS ===================== */
/* ================================================= */

private fun normalizeCategory(
    transactionCategory: String,
    plannedCategories: Set<String>
): String {
    return if (plannedCategories.contains(transactionCategory)) {
        transactionCategory
    } else {
        "Other"
    }
}
