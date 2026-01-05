package uk.ac.tees.mad.s3540722.pennypinch.data

import androidx.compose.runtime.mutableStateListOf

object TransactionRepository {

    private val transactions = mutableStateListOf<Transaction>()

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun getAllTransactions(): List<Transaction> = transactions

    // Category-based classification
    private val incomeCategories = listOf(
        "Salary", "Refund", "Bonus", "Savings", "Other Income"
    )

    private val expenseCategories = listOf(
        "Food", "Bills", "Shopping", "Travel", "Entertainment", "Other Expense"
    )

    fun getTotalIncome(): Double =
        transactions
            .filter { incomeCategories.contains(it.category) }
            .sumOf { it.amount }

    fun getTotalExpenses(): Double =
        transactions
            .filter { expenseCategories.contains(it.category) }
            .sumOf { it.amount }

    fun getBalance(): Double =
        getTotalIncome() - getTotalExpenses()
}
