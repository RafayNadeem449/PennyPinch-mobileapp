package uk.ac.tees.mad.s3540722.pennypinch.data

data class PlannedBudget(
    val periodType: String = "MONTHLY", // WEEKLY or MONTHLY
    val allocations: Map<String, Double> = emptyMap(), // category -> planned amount
    val createdAt: Long = System.currentTimeMillis()
)
