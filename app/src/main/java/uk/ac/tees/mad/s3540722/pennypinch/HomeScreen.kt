package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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

    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    var userName by remember { mutableStateOf("User") }

    var balance by remember { mutableStateOf(0.0) }
    var income by remember { mutableStateOf(0.0) }
    var expenses by remember { mutableStateOf(0.0) }
    var investments by remember { mutableStateOf(0.0) }

    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun loadData() {
        scope.launch {
            loading = true

            userName = FirebaseService.getUserName()
            val allTx = FirebaseService.getTransactions()

            transactions = allTx
            balance = FirebaseService.getBalance()
            income = allTx.filter { it.type == "Income" }.sumOf { it.amount }
            expenses = allTx.filter { it.type == "Expense" && !it.isCleared }.sumOf { it.amount }
            investments = FirebaseService.getTotalInvested()

            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    val dashboardTransactions = transactions
        .filter { it.type == "Income" || (it.type == "Expense" && !it.isCleared) }
        .sortedByDescending { it.timestamp }
        .take(5)

    val darkText = Color(0xFF1F2937)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome $userName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = { nav.navigate("profile") }) {
                    Text("Profile")
                }
                TextButton(
                    onClick = {
                        auth.signOut()
                        nav.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Logout")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- SUMMARY CARDS ---------- */
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedSummaryCard(
                    title = "Balance",
                    amount = balance,
                    backgroundColor = Color(0xFFFFE4CC),
                    textColor = darkText,
                    modifier = Modifier.weight(1f)
                )
                AnimatedSummaryCard(
                    title = "Income",
                    amount = income,
                    backgroundColor = Color(0xFFD6F2E3),
                    textColor = darkText,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedSummaryCard(
                    title = "Expenses",
                    amount = expenses,
                    backgroundColor = Color(0xFFFADADA),
                    textColor = darkText,
                    modifier = Modifier.weight(1f)
                )
                AnimatedSummaryCard(
                    title = "Investments",
                    amount = investments,
                    backgroundColor = Color(0xFFFFEFB3),
                    textColor = darkText,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /* ---------- TRANSACTIONS ---------- */
        Text(
            text = "Recent Activity",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

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

            if (dashboardTransactions.size > 1) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { nav.navigate("allTransactions") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View All Transactions")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { nav.navigate("investments") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View All Investments")
                    }
                }
            }
        }
    }
}

/* =========================================================
   ANIMATED SUMMARY CARD
   ========================================================= */

@Composable
fun AnimatedSummaryCard(
    title: String,
    amount: Double,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label = "AmountAnimation"
    )

    val formatter = NumberFormat.getCurrencyInstance(Locale.UK)

    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )

            Text(
                text = formatter.format(animatedValue),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
