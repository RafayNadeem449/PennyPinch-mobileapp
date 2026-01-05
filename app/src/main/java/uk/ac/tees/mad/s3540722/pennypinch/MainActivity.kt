package uk.ac.tees.mad.s3540722.pennypinch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.s3540722.pennypinch.navigation.AppNavigation
import uk.ac.tees.mad.s3540722.pennypinch.ui.theme.PennyPinchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            PennyPinchTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
