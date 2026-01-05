package uk.ac.tees.mad.s3540722.pennypinch.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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

    var type by remember { mutableStateOf("Expense") } // Income / Expense

    // Category logic
    val expenseCategories = listOf(
        "Bills", "Dinner", "Shopping", "Utilities", "General"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Bills") }
    var customCategory by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header
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

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (£)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Type selection
        Text("Type")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = type == "Income",
                    onClick = { type = "Income" }
                )
                Text("Income")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = type == "Expense",
                    onClick = { type = "Expense" }
                )
                Text("Expense")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORY (ONLY FOR EXPENSE)
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
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    expenseCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                                if (category != "General") {
                                    customCategory = ""
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom category field ONLY if General
            if (selectedCategory == "General") {
                TextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Write your category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                val amt = amount.toDoubleOrNull()
                if (title.isBlank() || amt == null || amt <= 0) {
                    Toast.makeText(
                        context,
                        "Please enter valid title and amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val finalCategory =
                    if (type == "Income") {
                        "Income"
                    } else {
                        if (selectedCategory == "General") {
                            if (customCategory.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please write a category",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            customCategory.trim()
                        } else {
                            selectedCategory
                        }
                    }

                scope.launch {
                    FirebaseService.addTransaction(
                        Transaction(
                            title = title.trim(),
                            amount = amt,
                            type = type,
                            category = finalCategory
                        )
                    )
                    Toast.makeText(context, "Transaction added", Toast.LENGTH_SHORT).show()
                    nav.popBackStack()
                }
            }
        ) {
            Text("Save Transaction")
        }
    }
}
