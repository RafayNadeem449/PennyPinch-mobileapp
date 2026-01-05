package uk.ac.tees.mad.s3540722.pennypinch.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun SignupScreen(nav: NavController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // FULL NAME FIELD
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // EMAIL FIELD
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // PASSWORD FIELD
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            // Update Firebase user profile with name
                            val user = auth.currentUser
                            val profileUpdate = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()

                            user?.updateProfile(profileUpdate)

                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()

                            nav.navigate("login")

                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.localizedMessage ?: "Signup failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}
