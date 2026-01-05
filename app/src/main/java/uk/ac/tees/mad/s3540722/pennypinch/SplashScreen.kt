package uk.ac.tees.mad.s3540722.pennypinch.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.s3540722.pennypinch.R
import uk.ac.tees.mad.s3540722.pennypinch.ui.theme.AccentGold
import uk.ac.tees.mad.s3540722.pennypinch.ui.theme.DeepTeal

@Composable
fun SplashScreen(nav: NavController) {

    var start by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        start = true
        delay(800)
        showTagline = true
        delay(1800)
        nav.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Logo scale animation
    val scale by animateFloatAsState(
        targetValue = if (start) 1f else 0.7f,
        animationSpec = tween(900, easing = OvershootInterpolatorEasing)
    )

    // Golden glow animation
    val glowScale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepTeal),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // GOLD GLOW BEHIND LOGO
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(glowScale)
                    .background(
                        AccentGold.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            )

            // LOGO with rounded corners (12dp)
            Image(
                painter = painterResource(id = R.drawable.penny_logo),
                contentDescription = "PennyPinch Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))   // 🔥 Rounded corners like border-radius
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))  // spacing between logo & text

            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(800)) + slideInVertically { it / 4 },
                exit = fadeOut()
            ) {
                Text(
                    text = "Smart Budgeting. Smarter You.",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

// Custom easing for pop animation
val OvershootInterpolatorEasing = Easing { fraction ->
    (fraction - 1).let { t -> 1 + 2.5f * t * t * t + t * t }
}
