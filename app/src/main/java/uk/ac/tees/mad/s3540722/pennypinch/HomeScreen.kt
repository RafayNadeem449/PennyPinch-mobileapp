package uk.ac.tees.mad.s3540722.pennypinch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle




@Composable
fun HomeScreen(nav: NavController) {

    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("User") }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var balance by remember { mutableStateOf(0.0) }
    var invested by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        scope.launch {
            userName = FirebaseService.getUserName()
            transactions = FirebaseService.getTransactions()
            balance = FirebaseService.getBalance()
            invested = FirebaseService.getTotalInvested()
        }
    }

    val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val expenses = transactions
        .filter { it.type == "Expense" && !it.isCleared }
        .sumOf { it.amount }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Welcome to PennyPinch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary   // Teal
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { nav.navigate("profile") }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "🚪",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.clickable {
                            nav.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }

        }

        /* ---------- SUMMARY CARDS ---------- */
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Balance", balance, Color(0xFFEAF1FF), Modifier.weight(1f))
                SummaryCard("Income", income, Color(0xFFEAF8F0), Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Expenses", expenses, Color(0xFFFFE4E6), Modifier.weight(1f))
                SummaryCard("Invested", invested, Color(0xFFFFF7D6), Modifier.weight(1f))
            }
        }

        /* ---------- DASHBOARD PIE (WITH LEGEND) ---------- */
        item {
            DashboardSnapshotPie(
                income = income,
                expenses = expenses,
                investments = invested,
                onViewBudget = { nav.navigate("budgetInsights") }
            )
        }

        item {
            Button(
                onClick = { nav.navigate("addTransaction") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Add Transaction")
            }
        }

        /* ---------- RECENT TRANSACTIONS ---------- */
        item {
            Text("Recent Transactions", fontWeight = FontWeight.Bold)
        }

        items(transactions.take(5)) { tx ->
            TransactionRow(tx)
        }

        if (transactions.size > 1) {
            item {
                Button(
                    onClick = { nav.navigate("allTransactions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View All Transactions →")
                }
            }
        }

        item {
            Button(
                onClick = { nav.navigate("budgetSetup") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Create Budget")
            }
        }

        item {
            Button(
                onClick = { nav.navigate("investments") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("View All Investments →")
            }
        }


    }
}

/* ========================================================= */
/* ===================== COMPONENTS ======================== */
/* ========================================================= */

@Composable
private fun SummaryCard(
    title: String,
    value: Double,
    bgColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "£${"%.2f".format(value)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TransactionRow(tx: Transaction) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(tx.title, fontWeight = FontWeight.Medium)
                Text(tx.category, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "£${"%.2f".format(tx.amount)}",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ========================================================= */
/* ================= DASHBOARD PIE ========================= */
/* ========================================================= */

@Composable
private fun DashboardSnapshotPie(
    income: Double,
    expenses: Double,
    investments: Double,
    onViewBudget: () -> Unit
) {
    val total = income + expenses + investments
    if (total <= 0) return

    val data = listOf(
        "Income" to income,
        "Expenses" to expenses,
        "Investments" to investments
    )

    val colors = listOf(
        Color(0xFF2EC4B6), // Teal (Income)
        Color(0xFFFF6B6B), // Soft Red (Expenses)
        Color(0xFFFFC857)  // Amber (Investments)
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Financial Snapshot", fontWeight = FontWeight.Bold)
                Text("☰")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /* ---------- PIE ---------- */
                Canvas(modifier = Modifier.size(140.dp)) {
                    var startAngle = -90f
                    data.forEachIndexed { index, pair ->
                        val sweep = (pair.second / total * 360f).toFloat()
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                        startAngle += sweep
                    }
                }

                /* ---------- LEGEND ---------- */
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    data.forEachIndexed { index, pair ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(colors[index], CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(pair.first, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "£${"%.2f".format(pair.second)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "View Budget Insights →",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewBudget() }
            )
        }
    }
}
