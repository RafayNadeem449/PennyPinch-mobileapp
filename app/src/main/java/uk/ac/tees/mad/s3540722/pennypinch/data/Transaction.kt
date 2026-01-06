package uk.ac.tees.mad.s3540722.pennypinch.data

data class Transaction(
    val id: String = "",
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,              // Income / Expense
    val isCleared: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
