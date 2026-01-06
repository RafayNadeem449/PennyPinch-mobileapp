package uk.ac.tees.mad.s3540722.pennypinch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.s3540722.pennypinch.ui.*
import uk.ac.tees.mad.s3540722.pennypinch.ui.screens.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignupScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("addTransaction") {
            AddTransactionScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("allTransactions") {
            AllTransactionsScreen(navController)
        }
    }
}
