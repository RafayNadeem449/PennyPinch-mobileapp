package uk.ac.tees.mad.s3540722.pennypinch.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ----------------------------
// PennyPinch Color Scheme
// ----------------------------

val DeepTeal = Color(0xFF008080)
val SoftMint = Color(0xFF98FB98)
val WarmGray = Color(0xFFA9A9A9)
val AccentGold = Color(0xFFB8860B)

private val LightColors = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,

    secondary = SoftMint,
    onSecondary = Color.Black,

    background = Color.White,
    surface = Color.White,

    primaryContainer = WarmGray,
    onPrimaryContainer = Color.White,

    secondaryContainer = SoftMint,
    onSecondaryContainer = Color.Black,

    tertiary = AccentGold,
)

@Composable
fun PennyPinchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
