package uk.ac.tees.mad.s3540722.pennypinch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirestoreService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction

@Composable
fun HomeScreen(nav: androidx.navigation.NavController) {

    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            transactions = FirestoreService.getUserTransactions()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Your Transactions", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { nav.navigate("addTransaction") }) {
            Text("Add Transaction")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tx.title, style = MaterialTheme.typography.titleMedium)
                        Text("£${tx.amount}")
                        Text("To: ${tx.receiverEmail}")
                        Text("Category: ${tx.category}")
                    }
                }
            }
        }
    }
}
