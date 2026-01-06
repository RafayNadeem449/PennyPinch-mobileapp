package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(nav: NavController) {

    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("User") }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    var income by remember { mutableStateOf(0.0) }
    var expenses by remember { mutableStateOf(0.0) }
    var balance by remember { mutableStateOf(0.0) }

    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale.UK)
    }

    val animIncome by animateFloatAsState(income.toFloat(), tween(500), label = "inc")
    val animExpense by animateFloatAsState(expenses.toFloat(), tween(500), label = "exp")
    val animBalance by animateFloatAsState(balance.toFloat(), tween(500), label = "bal")

    fun loadData() {
        scope.launch {
            loading = true
            userName = FirebaseService.getUserName()
            transactions = FirebaseService.getTransactions()

            income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
            expenses = transactions.filter { it.type == "Expense" && !it.isCleared }.sumOf { it.amount }
            balance = income - expenses

            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    // ✅ DASHBOARD FILTER (FINAL LOGIC)
    val dashboardTransactions = transactions
        .filter { it.type == "Income" || (it.type == "Expense" && !it.isCleared) }
        .sortedByDescending { it.timestamp }
        .take(5)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome $userName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "👤",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { nav.navigate("profile") }
                )

                Text(
                    text = "🚪",
                    fontSize = 22.sp,
                    modifier = Modifier.clickable {
                        auth.signOut()
                        nav.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }

        if (loading) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }

        /* ---------- SUMMARY ---------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard("Balance", formatter.format(animBalance), Color(0xFFB2DFDB), Color.Black, Modifier.weight(1f))
                SummaryCard("Income", formatter.format(animIncome), Color(0xFFFFCCBC), Color(0xFF2E7D32), Modifier.weight(1f))
                SummaryCard("Expenses", formatter.format(animExpense), Color(0xFFFFCDD2), Color(0xFFC62828), Modifier.weight(1f))
            }
        }

        item {
            Button(
                onClick = { nav.navigate("addTransaction") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add Transaction") }
        }

        item {
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (dashboardTransactions.isEmpty() && !loading) {
            item { Text("No recent activity 🎉") }
        } else {
            items(dashboardTransactions) { tx ->
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

        if (dashboardTransactions.size > 1) {
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

/* ---------- SUMMARY CARD ---------- */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    bg: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
