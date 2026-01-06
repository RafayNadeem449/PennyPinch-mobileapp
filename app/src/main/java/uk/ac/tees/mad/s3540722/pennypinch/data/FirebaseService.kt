package uk.ac.tees.mad.s3540722.pennypinch.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /* ---------------- USER PROFILE ---------------- */

    suspend fun createUser(fullName: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(uid)
            .set(mapOf("fullName" to fullName))
            .await()
    }

    suspend fun getUserName(): String {
        val uid = auth.currentUser?.uid ?: return "User"
        val snap = db.collection("users").document(uid).get().await()
        return snap.getString("fullName") ?: "User"
    }

    suspend fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    /* ---------------- TRANSACTIONS ---------------- */

    suspend fun getTransactions(): List<Transaction> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("transactions")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            Transaction(
                id = doc.id,   // ✅ NOW EXISTS
                title = doc.getString("title") ?: return@mapNotNull null,
                amount = doc.getDouble("amount") ?: return@mapNotNull null,
                category = doc.getString("category") ?: "",
                type = doc.getString("type") ?: "",
                isCleared = doc.getBoolean("isCleared") ?: false,
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
            )
        }
    }

    suspend fun addTransaction(transaction: Transaction) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "userId" to uid,
            "title" to transaction.title,
            "amount" to transaction.amount,
            "category" to transaction.category,
            "type" to transaction.type,
            "isCleared" to transaction.isCleared,
            "timestamp" to transaction.timestamp
        )

        db.collection("transactions")
            .add(data)
            .await()
    }


    suspend fun getBalance(): Double {
        val txs = getTransactions()
        val income = txs.filter { it.type == "Income" }.sumOf { it.amount }
        val expenses = txs.filter { it.type == "Expense" && !it.isCleared }.sumOf { it.amount }
        return income - expenses
    }

    suspend fun markExpenseCleared(tx: Transaction) {
        if (tx.id.isBlank()) return

        db.collection("transactions")
            .document(tx.id)   // ✅ NOW EXISTS
            .update("isCleared", true)
            .await()
    }
}
