package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction
import uk.ac.tees.mad.s3540722.pennypinch.ui.util.DateUtils

@Composable
fun AllTransactionsScreen(nav: NavController) {

    val scope = rememberCoroutineScope()
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            transactions = FirebaseService.getTransactions()
                .sortedByDescending { it.timestamp }
            loading = false
        }
    }

    val grouped = transactions.groupBy {
        DateUtils.formatMonthYear(it.timestamp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("All Transactions", style = MaterialTheme.typography.titleLarge)
            Text(
                "Back",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { nav.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            grouped.forEach { (month, txs) ->

                item {
                    Text(
                        text = month,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(txs.size) { index ->
                    val tx = txs[index]
                    TransactionCard(
                        tx = tx,
                        onClear = {
                            scope.launch {
                                FirebaseService.markExpenseCleared(tx)
                                transactions = FirebaseService.getTransactions()
                                    .sortedByDescending { it.timestamp }
                            }
                        }
                    )
                }
            }
        }
    }
}
