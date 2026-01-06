package uk.ac.tees.mad.s3540722.pennypinch.data

data class Investment(
    val id: String = "",
    val amount: Double,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
