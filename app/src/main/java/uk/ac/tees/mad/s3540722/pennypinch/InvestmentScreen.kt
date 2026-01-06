package uk.ac.tees.mad.s3540722.pennypinch.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import uk.ac.tees.mad.s3540722.pennypinch.data.Investment
import uk.ac.tees.mad.s3540722.pennypinch.ui.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentsScreen(nav: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val categories = listOf("Stocks", "Crypto", "Bonds", "Real Estate")

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var expanded by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(true) }
    var investments by remember { mutableStateOf<List<Investment>>(emptyList()) }

    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale.UK)
    }

    fun load() {
        scope.launch {
            loading = true
            investments = FirebaseService.getInvestments()
            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    val totalsByCategory = remember(investments) {
        categories.associateWith { cat ->
            investments.filter { it.category == cat }.sumOf { it.amount }
        }
    }

    val totalInvested = totalsByCategory.values.sum()

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
            Text(
                text = "Investments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { nav.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "“Investing for the future is important.”",
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- TOTAL INVESTED ---------- */
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Invested", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatter.format(totalInvested),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- PIE CHART ---------- */
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Breakdown", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {

                    InvestmentPieChart(
                        totalsByCategory = totalsByCategory,
                        total = totalInvested,
                        modifier = Modifier.size(140.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.forEach { cat ->
                            Text("$cat: ${formatter.format(totalsByCategory[cat] ?: 0.0)}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- ADD INVESTMENT ---------- */
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text("Add Investment", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (£)") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            FirebaseService.addInvestment(
                                Investment(
                                    amount = amount,
                                    category = selectedCategory
                                )
                            )
                            amountText = ""
                            Toast.makeText(context, "Investment Added", Toast.LENGTH_SHORT).show()
                            load()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Investment")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            return@Column
        }

        /* ---------- INVESTMENT LIST ---------- */
        Text("Recent Investments", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(investments) { inv ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(inv.category, fontWeight = FontWeight.Bold)
                            Text(formatter.format(inv.amount), fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = DateUtils.formatTransactionDate(inv.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/* ---------- PIE CHART ---------- */
@Composable
private fun InvestmentPieChart(
    totalsByCategory: Map<String, Double>,
    total: Double,
    modifier: Modifier = Modifier
) {
    if (total <= 0.0) {
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No data")
        }
        return
    }

    val baseColor = MaterialTheme.colorScheme.primary
    val colors = listOf(
        baseColor.copy(alpha = 0.9f),
        baseColor.copy(alpha = 0.7f),
        baseColor.copy(alpha = 0.5f),
        baseColor.copy(alpha = 0.3f)
    )

    Canvas(modifier = modifier) {
        var startAngle = -90f

        totalsByCategory.values.forEachIndexed { index, value ->
            val sweepAngle = (value / total * 360f).toFloat()

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )

            startAngle += sweepAngle
        }
    }
}
