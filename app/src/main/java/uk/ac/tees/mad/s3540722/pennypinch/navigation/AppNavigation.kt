package uk.ac.tees.mad.s3540722.pennypinch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.s3540722.pennypinch.BudgetInsightsScreen
import uk.ac.tees.mad.s3540722.pennypinch.BudgetSetupScreen
import uk.ac.tees.mad.s3540722.pennypinch.HomeScreen
import uk.ac.tees.mad.s3540722.pennypinch.ui.*
import uk.ac.tees.mad.s3540722.pennypinch.ui.screens.LoginScreen
import uk.ac.tees.mad.s3540722.pennypinch.ui.screens.SplashScreen
import uk.ac.tees.mad.s3540722.pennypinch.ui.InvestmentsScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        /* ---------- SPLASH ---------- */
        composable("splash") {
            SplashScreen(navController)
        }

        /* ---------- AUTH ---------- */
        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignupScreen(navController)
        }

        /* ---------- HOME ---------- */
        composable("home") {
            HomeScreen(navController)
        }

        composable("addTransaction") {
            AddTransactionScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        /* ---------- TRANSACTIONS ---------- */
        composable("allTransactions") {
            AllTransactionsScreen(navController)
        }

        /* ---------- INVESTMENTS ---------- */
        composable("investments") {
            InvestmentsScreen(navController)
        }

        /* ---------- BUDGET ---------- */
        composable("budgetSetup") {
            BudgetSetupScreen(navController)
        }

        composable("budgetInsights") {
            BudgetInsightsScreen(navController)
        }
    }
}
