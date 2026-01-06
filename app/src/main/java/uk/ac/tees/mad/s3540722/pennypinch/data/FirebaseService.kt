package uk.ac.tees.mad.s3540722.pennypinch.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /* =========================================================
       USER PROFILE
       ========================================================= */

    suspend fun createUser(fullName: String) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "fullName" to fullName
        )

        db.collection("users")
            .document(uid)
            .set(data)
            .await()
    }

    suspend fun getUserName(): String {
        val uid = auth.currentUser?.uid ?: return "User"

        val snap = db.collection("users")
            .document(uid)
            .get()
            .await()

        return snap.getString("fullName") ?: "User"
    }

    suspend fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    /* =========================================================
       TRANSACTIONS
       ========================================================= */

    suspend fun addTransaction(transaction: Transaction) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "userId" to uid,
            "title" to transaction.title,
            "amount" to transaction.amount,
            "category" to transaction.category,
            "type" to transaction.type,          // Income / Expense
            "isCleared" to transaction.isCleared,
            "timestamp" to transaction.timestamp
        )

        db.collection("transactions")
            .add(data)
            .await()
    }

    suspend fun getTransactions(): List<Transaction> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("transactions")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val title = doc.getString("title") ?: return@mapNotNull null
            val amount = doc.getDouble("amount") ?: return@mapNotNull null
            val category = doc.getString("category") ?: return@mapNotNull null
            val type = doc.getString("type") ?: return@mapNotNull null
            val isCleared = doc.getBoolean("isCleared") ?: false
            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

            Transaction(
                id = doc.id,
                title = title,
                amount = amount,
                category = category,
                type = type,
                isCleared = isCleared,
                timestamp = timestamp
            )
        }.sortedByDescending { it.timestamp }
    }

    suspend fun markExpenseCleared(transaction: Transaction) {
        if (transaction.id.isBlank()) return

        db.collection("transactions")
            .document(transaction.id)
            .update("isCleared", true)
            .await()
    }

    suspend fun getBalance(): Double {
        val txs = getTransactions()

        val income = txs
            .filter { it.type == "Income" }
            .sumOf { it.amount }

        val expenses = txs
            .filter { it.type == "Expense" && !it.isCleared }
            .sumOf { it.amount }

        return income - expenses
    }

    /* =========================================================
       INVESTMENTS
       ========================================================= */

    suspend fun addInvestment(investment: Investment) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "userId" to uid,
            "amount" to investment.amount,
            "category" to investment.category,
            "timestamp" to investment.timestamp
        )

        db.collection("investments")
            .document(uid)
            .collection("items")
            .add(data)
            .await()
    }

    suspend fun getInvestments(): List<Investment> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("investments")
            .document(uid)
            .collection("items")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val amount = doc.getDouble("amount") ?: return@mapNotNull null
            val category = doc.getString("category") ?: return@mapNotNull null
            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

            Investment(
                id = doc.id,
                amount = amount,
                category = category,
                timestamp = timestamp
            )
        }.sortedByDescending { it.timestamp }
    }

    suspend fun getTotalInvested(): Double {
        return getInvestments().sumOf { it.amount }
    }
}
