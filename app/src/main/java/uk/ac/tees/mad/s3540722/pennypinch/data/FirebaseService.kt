package uk.ac.tees.mad.s3540722.pennypinch.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String = auth.currentUser!!.uid

    // ---------- USER ----------
    suspend fun createUser(name: String, email: String) {
        val data = mapOf(
            "name" to name,
            "email" to email,
            "balance" to 0.0
        )
        db.collection("users").document(uid()).set(data).await()
    }

    suspend fun getUserName(): String {
        val doc = db.collection("users").document(uid()).get().await()
        return doc.getString("name") ?: "User"
    }

    suspend fun getBalance(): Double {
        val doc = db.collection("users").document(uid()).get().await()
        return doc.getDouble("balance") ?: 0.0
    }

    // ---------- TRANSACTIONS ----------
    suspend fun addTransaction(tx: Transaction) {
        val ref = db.collection("users")
            .document(uid())
            .collection("transactions")

        ref.add(tx).await()

        val balance = getBalance()
        val newBalance =
            if (tx.type == "Income") balance + tx.amount
            else balance - tx.amount

        db.collection("users")
            .document(uid())
            .set(mapOf("balance" to newBalance), SetOptions.merge())
            .await()
    }

    suspend fun getTransactions(): List<Transaction> {
        val snap = db.collection("users")
            .document(uid())
            .collection("transactions")
            .orderBy("timestamp")
            .get()
            .await()

        return snap.toObjects(Transaction::class.java)
    }

    // ---------- CATEGORIES ----------
    suspend fun addCategory(name: String) {
        db.collection("users")
            .document(uid())
            .collection("categories")
            .add(Category(name))
            .await()
    }

    suspend fun getCategories(): List<String> {
        val snap = db.collection("users")
            .document(uid())
            .collection("categories")
            .get()
            .await()

        return snap.documents.mapNotNull { it.getString("name") }
    }
}
