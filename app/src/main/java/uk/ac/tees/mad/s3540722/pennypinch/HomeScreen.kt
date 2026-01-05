package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction

@Composable
fun HomeScreen(nav: NavController) {

    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("User") }
    var balance by remember { mutableStateOf(0.0) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun loadData() {
        scope.launch {
            loading = true
            userName = FirebaseService.getUserName()
            balance = FirebaseService.getBalance()
            transactions = FirebaseService.getTransactions()
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val totalIncome = transactions
        .filter { it.type == "Income" }
        .sumOf { it.amount }

    val totalExpenses = transactions
        .filter { it.type == "Expense" && !it.isCleared }
        .sumOf { it.amount }

    val visibleTransactions = transactions.take(10)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        /* ---------------- HEADER ---------------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome $userName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Logout",
                    color = Color.Red,
                    modifier = Modifier.clickable {
                        auth.signOut()
                        nav.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }

        /* ---------------- LOADING ---------------- */
        if (loading) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        /* ---------------- SUMMARY CARDS ---------------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Balance",
                    value = "£$balance",
                    backgroundColor = Color(0xFFB2DFDB),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Income",
                    value = "£$totalIncome",
                    backgroundColor = Color(0xFFFFCCBC),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Expenses",
                    value = "£$totalExpenses",
                    backgroundColor = Color(0xFFFFCDD2),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        /* ---------------- ADD BUTTON ---------------- */
        item {
            Button(
                onClick = { nav.navigate("addTransaction") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction")
            }
        }

        /* ---------------- TITLE ---------------- */
        item {
            Text(
                text = "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        /* ---------------- TRANSACTIONS ---------------- */
        if (visibleTransactions.isEmpty() && !loading) {
            item {
                Text("No transactions yet")
            }
        } else {
            items(visibleTransactions) { tx ->
                TransactionCard(
                    tx = tx,
                    onClear = {
                        scope.launch {
                            FirebaseService.markExpenseCleared(tx)
                            loadData()
                        }
                    }
                )
            }
        }

        /* ---------------- VIEW ALL ---------------- */
        if (transactions.size > 10) {
            item {
                Button(
                    onClick = { nav.navigate("allTransactions") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Transactions")
                }
            }
        }
    }
}

/* ---------------- SUMMARY CARD ---------------- */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
