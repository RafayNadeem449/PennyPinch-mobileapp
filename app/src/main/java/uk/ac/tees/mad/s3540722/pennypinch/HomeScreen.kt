package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

    // 🔁 Load data from Firebase
    fun loadData() {
        scope.launch {
            loading = true
            userName = FirebaseService.getUserName()
            balance = FirebaseService.getBalance()
            transactions = FirebaseService.getTransactions()
            loading = false
        }
    }

    // Load when screen is first shown
    LaunchedEffect(Unit) {
        loadData()
    }

    val totalIncome = transactions
        .filter { it.type == "Income" }
        .sumOf { it.amount }

    val totalExpenses = transactions
        .filter { it.type == "Expense" }
        .sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Header
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

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔹 Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Balance",
                value = "£$balance",
                color = Color(0xFFB2DFDB),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Income",
                value = "£$totalIncome",
                color = Color(0xFFFFCCBC),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Expenses",
                value = "£$totalExpenses",
                color = Color(0xFFFFCDD2),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Add Transaction Button
        Button(
            onClick = { nav.navigate("addTransaction") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Transaction")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Transactions List
        Text(
            text = "Recent Transactions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!loading && transactions.isEmpty()) {
            Text("No transactions yet")
        }

        transactions.forEach { tx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(tx.title, fontWeight = FontWeight.Medium)
                        Text("${tx.type} • ${tx.category}")
                    }
                    Text("£${tx.amount}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
