package uk.ac.tees.mad.s3540722.pennypinch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import uk.ac.tees.mad.s3540722.pennypinch.data.FirebaseService
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(nav: NavController) {

    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var createdOn by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            fullName = FirebaseService.getUserName()
            email = FirebaseService.getUserEmail()

            val createdTimestamp = auth.currentUser
                ?.metadata
                ?.creationTimestamp

            createdOn = createdTimestamp?.let {
                SimpleDateFormat(
                    "dd MMM yyyy · hh:mm a",
                    Locale.UK
                ).format(Date(it))
            } ?: "-"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { nav.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        /* ---------- FULL NAME ---------- */
        OutlinedTextField(
            value = fullName,
            onValueChange = {},
            label = { Text("Full Name") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- EMAIL ---------- */
        OutlinedTextField(
            value = email,
            onValueChange = {},
            label = { Text("Email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- ACCOUNT CREATED ---------- */
        OutlinedTextField(
            value = createdOn,
            onValueChange = {},
            label = { Text("Account Created On") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        /* ---------- WELCOME MESSAGE ---------- */
        Text(
            text = "We are Happy to Have you in PennyPinch",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
