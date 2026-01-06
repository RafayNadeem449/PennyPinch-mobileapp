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
    var balance by remember { mutableStateOf(0.0) }
    var income by remember { mutableStateOf(0.0) }
    var expenses by remember { mutableStateOf(0.0) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.UK)
    }

    // 🔹 Animated values
    val animatedBalance by animateFloatAsState(balance.toFloat(), tween(600), label = "balance")
    val animatedIncome by animateFloatAsState(income.toFloat(), tween(600), label = "income")
    val animatedExpenses by animateFloatAsState(expenses.toFloat(), tween(600), label = "expenses")

    fun loadData() {
        scope.launch {
            loading = true
            userName = FirebaseService.getUserName()
            transactions = FirebaseService.getTransactions()

            income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
            expenses = transactions.filter { it.type == "Expense" && !it.isCleared }.sumOf { it.amount }
            balance = FirebaseService.getBalance()

            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    val visibleTransactions = transactions.take(10)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

                // 👤 Profile
                Text(
                    text = "👤",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { nav.navigate("profile") }
                )

                // 🚪 Logout
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

        /* ---------- SUMMARY CARDS ---------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Balance",
                    value = currencyFormatter.format(animatedBalance),
                    backgroundColor = Color(0xFFB2DFDB),
                    valueColor = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Income",
                    value = currencyFormatter.format(animatedIncome),
                    backgroundColor = Color(0xFFFFCCBC),
                    valueColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Expenses",
                    value = currencyFormatter.format(animatedExpenses),
                    backgroundColor = Color(0xFFFFCDD2),
                    valueColor = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        /* ---------- ADD TRANSACTION ---------- */
        item {
            Button(
                onClick = { nav.navigate("addTransaction") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction")
            }
        }

        /* ---------- TRANSACTIONS ---------- */
        item {
            Text(
                text = "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (visibleTransactions.isEmpty() && !loading) {
            item { Text("No transactions yet") }
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

/* ---------- SUMMARY CARD ---------- */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    backgroundColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
        }
    }
}
