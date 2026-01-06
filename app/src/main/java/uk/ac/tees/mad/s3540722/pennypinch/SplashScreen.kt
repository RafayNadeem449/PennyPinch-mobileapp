package uk.ac.tees.mad.s3540722.pennypinch.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    LaunchedEffect(Unit) {
        start = true
        delay(800)
        showTagline = true
        delay(1800)
        nav.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (start) 1f else 0.75f,
        animationSpec = tween(900, easing = OvershootInterpolatorEasing),
        label = "logoScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(glowScale)
                    .background(
                        AccentGold.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.penny_logo),
                contentDescription = "PennyPinch Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(800)) + slideInVertically { it / 4 }
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

val OvershootInterpolatorEasing = Easing { fraction ->
    (fraction - 1).let { t -> 1 + 2.5f * t * t * t + t * t }
}
