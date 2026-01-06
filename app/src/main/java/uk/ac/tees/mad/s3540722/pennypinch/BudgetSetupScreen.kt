package uk.ac.tees.mad.s3540722.pennypinch

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.PlannedBudget

@Composable
fun BudgetSetupScreen(nav: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var periodType by remember { mutableStateOf("MONTHLY") }

    // keep categories small for now (we can expand later)
    val categories = listOf("Food", "Bills", "Transport", "Shopping", "Utilities", "Other")

    val fields = remember {
        mutableStateMapOf<String, String>().apply {
            categories.forEach { put(it, "") }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Budget Setup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Back", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { nav.popBackStack() })
            }
        }

        item {
            Text("Choose Budget Type", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = periodType == "WEEKLY",
                    onClick = { periodType = "WEEKLY" },
                    label = { Text("Weekly") }
                )
                FilterChip(
                    selected = periodType == "MONTHLY",
                    onClick = { periodType = "MONTHLY" },
                    label = { Text("Monthly") }
                )
            }
        }

        item {
            Text("Allocate Planned Amounts (£)", fontWeight = FontWeight.Bold)
            Text("Tip: Only fill the categories you care about.")
        }

        categories.forEach { cat ->
            item {
                OutlinedTextField(
                    value = fields[cat] ?: "",
                    onValueChange = { fields[cat] = it },
                    label = { Text("$cat (£)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Button(
                onClick = {
                    val allocations = categories.associateWith { (fields[it]?.toDoubleOrNull() ?: 0.0) }
                        .filterValues { it > 0.0 }

                    if (allocations.isEmpty()) {
                        Toast.makeText(context, "Enter at least one category amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        FirebaseService.savePlannedBudget(
                            PlannedBudget(
                                periodType = periodType,
                                allocations = allocations
                            )
                        )
                        Toast.makeText(context, "Budget saved!", Toast.LENGTH_SHORT).show()
                        nav.navigate("budgetInsights")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Budget")
            }
        }
    }
}
