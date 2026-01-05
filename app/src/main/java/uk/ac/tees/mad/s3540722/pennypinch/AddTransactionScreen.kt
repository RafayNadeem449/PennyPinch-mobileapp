package uk.ac.tees.mad.s3540722.pennypinch.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirestoreService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction

@Composable
fun AddTransactionScreen(nav: NavController) {

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("expense") }
    var receiverEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Add Transaction", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (£)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = receiverEmail,
            onValueChange = { receiverEmail = it },
            label = { Text("Send To (Friend Email)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row {
            RadioButton(
                selected = category == "income",
                onClick = { category = "income" }
            )
            Text("Income")

            Spacer(Modifier.width(20.dp))

            RadioButton(
                selected = category == "expense",
                onClick = { category = "expense" }
            )
            Text("Expense")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank() || amount.isBlank()) {
                    Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val tx = Transaction(
                    title = title,
                    amount = amount.toDouble(),
                    category = category,
                    receiverEmail = receiverEmail
                )

                CoroutineScope(Dispatchers.IO).launch {
                    FirestoreService.addTransaction(tx)
                }

                Toast.makeText(context, "Transaction saved!", Toast.LENGTH_SHORT).show()
                nav.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
