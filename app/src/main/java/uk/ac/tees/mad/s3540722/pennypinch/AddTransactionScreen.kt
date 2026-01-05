package uk.ac.tees.mad.s3540722.pennypinch.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(nav: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }

    val categories = listOf("Bills", "Dinner", "Shopping", "Utilities", "General")
    var selectedCategory by remember { mutableStateOf("Bills") }
    var customCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- HEADER WITH BACK ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { nav.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- TITLE ---------- */
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- AMOUNT ---------- */
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (£)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- TYPE ---------- */
        Row {
            RadioButton(type == "Income", { type = "Income" })
            Text("Income")
            Spacer(Modifier.width(16.dp))
            RadioButton(type == "Expense", { type = "Expense" })
            Text("Expense")
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- CATEGORY (ONLY FOR EXPENSE) ---------- */
        if (type == "Expense") {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedCategory = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (selectedCategory == "General") {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Write your category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* ---------- SAVE ---------- */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val amt = amount.toDoubleOrNull()
                if (title.isBlank() || amt == null) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val finalCategory =
                    if (type == "Income") "Income"
                    else if (selectedCategory == "General") customCategory
                    else selectedCategory

                scope.launch {
                    FirebaseService.addTransaction(
                        Transaction(
                            title = title,
                            amount = amt,
                            type = type,
                            category = finalCategory
                        )
                    )
                    nav.popBackStack()
                }
            }
        ) {
            Text("Save Transaction")
        }
    }
}
