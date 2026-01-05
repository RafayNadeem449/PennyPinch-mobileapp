package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction

@Composable
fun TransactionCard(tx: Transaction, onClear: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(12.dp), Arrangement.SpaceBetween) {
            Column {
                Text(tx.title, fontWeight = FontWeight.Medium)
                Text("${tx.type} • ${tx.category}")
                if (tx.isCleared) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Cleared") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFF4CAF50),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("£${tx.amount}", fontWeight = FontWeight.Bold)
                if (tx.type == "Expense" && !tx.isCleared) {
                    TextButton(onClick = onClear) { Text("Mark Cleared") }
                }
            }
        }
    }
}
