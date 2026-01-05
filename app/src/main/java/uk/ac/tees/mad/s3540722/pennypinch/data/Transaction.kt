package uk.ac.tees.mad.s3540722.pennypinch.data

data class Transaction(
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val receiverEmail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
