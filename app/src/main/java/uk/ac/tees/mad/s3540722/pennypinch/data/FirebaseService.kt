package uk.ac.tees.mad.s3540722.pennypinch.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid() = auth.currentUser!!.uid

    // ---------- USER ----------
    suspend fun createUser(name: String, email: String) {
        db.collection("users").document(uid()).set(
            mapOf(
                "name" to name,
                "email" to email,
                "balance" to 0.0
            )
        ).await()
    }

    suspend fun getUserName(): String =
        db.collection("users").document(uid()).get().await()
            .getString("name") ?: "User"

    suspend fun getBalance(): Double =
        db.collection("users").document(uid()).get().await()
            .getDouble("balance") ?: 0.0

    // ---------- TRANSACTIONS ----------
    suspend fun addTransaction(tx: Transaction) {
        db.collection("users")
            .document(uid())
            .collection("transactions")
            .add(tx)
            .await()

        val delta =
            if (tx.type == "Income") tx.amount
            else -tx.amount

        db.collection("users")
            .document(uid())
            .update("balance", FieldValue.increment(delta))
            .await()
    }

    suspend fun getTransactions(): List<Transaction> =
        db.collection("users")
            .document(uid())
            .collection("transactions")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Transaction::class.java)

    suspend fun markExpenseCleared(tx: Transaction) {
        val snap = db.collection("users")
            .document(uid())
            .collection("transactions")
            .whereEqualTo("timestamp", tx.timestamp)
            .get()
            .await()

        if (snap.isEmpty) return

        snap.documents.first().reference
            .set(mapOf("isCleared" to true), SetOptions.merge())
            .await()

        db.collection("users")
            .document(uid())
            .update("balance", FieldValue.increment(tx.amount))
            .await()
    }
}
