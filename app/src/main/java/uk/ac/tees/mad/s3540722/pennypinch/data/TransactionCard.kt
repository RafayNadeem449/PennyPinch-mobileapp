package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.animation.*
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
import uk.ac.tees.mad.s3540722.pennypinch.data.Transaction
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionCard(
    tx: Transaction,
    onClear: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale.UK)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9EE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(tx.title, fontWeight = FontWeight.Bold)
                Text(formatter.format(tx.amount), fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("${tx.type} • ${tx.category}", fontSize = 14.sp, color = Color.Gray)

                if (tx.type == "Expense") {

                    AnimatedVisibility(
                        visible = tx.isCleared,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text("DONE", fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF2E7D32),
                                labelColor = Color.White
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = !tx.isCleared
                    ) {
                        TextButton(onClick = { showDialog = true }) {
                            Text("Clear")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Clear Expense") },
            text = { Text("Are you sure you want to mark this expense as paid?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onClear()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
