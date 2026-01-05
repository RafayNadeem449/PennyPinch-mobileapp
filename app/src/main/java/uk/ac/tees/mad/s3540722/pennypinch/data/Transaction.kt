package uk.ac.tees.mad.s3540722.pennypinch.data

data class Transaction(
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "Expense", // Income / Expense
    val category: String = "General",
    val isCleared: Boolean = false, // ✅ NEW
    val timestamp: Long = System.currentTimeMillis()
)
