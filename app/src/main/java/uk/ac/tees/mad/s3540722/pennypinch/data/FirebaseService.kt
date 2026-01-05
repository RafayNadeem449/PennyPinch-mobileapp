package uk.ac.tees.mad.s3540722.pennypinch.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirestoreService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Save user profile after signup
    suspend fun createUser(name: String, email: String) {
        val uid = auth.currentUser?.uid ?: return

        val userData = mapOf(
            "name" to name,
            "email" to email,
            "balance" to 0.0
        )

        db.collection("users")
            .document(uid)
            .set(userData, SetOptions.merge())
            .await()
    }

    // Add a new transaction to Firestore
    suspend fun addTransaction(tx: Transaction) {
        val senderId = auth.currentUser?.uid ?: return

        val data = mapOf(
            "senderId" to senderId,
            "receiverEmail" to tx.receiverEmail,
            "title" to tx.title,
            "amount" to tx.amount,
            "category" to tx.category,
            "timestamp" to tx.timestamp
        )

        db.collection("transactions")
            .add(data)
            .await()
    }

    // Get all transactions for current user
    suspend fun getUserTransactions(): List<Transaction> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("transactions")
            .whereEqualTo("senderId", uid)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val title = doc.getString("title") ?: return@mapNotNull null
            val amount = doc.getDouble("amount") ?: return@mapNotNull null
            val category = doc.getString("category") ?: return@mapNotNull null
            val receiverEmail = doc.getString("receiverEmail") ?: ""
            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

            Transaction(
                title = title,
                amount = amount,
                category = category,
                receiverEmail = receiverEmail,
                timestamp = timestamp
            )
        }
    }
}
