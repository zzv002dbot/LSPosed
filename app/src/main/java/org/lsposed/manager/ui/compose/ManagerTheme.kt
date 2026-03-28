package org.lsposed.manager.ui.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CatppuccinBlue = Color(0xFF8AADF4)
private val CatppuccinLavender = Color(0xFFB7BDF8)
private val CatppuccinGreen = Color(0xFFA6DA95)
private val CatppuccinSky = Color(0xFF7DC4E4)
private val CatppuccinPink = Color(0xFFF5BDE6)
private val AmoledBlack = Color(0xFF000000)
private val DarkGrey = Color(0xFF363A4F)

private fun Color.blend(other: Color, ratio: Float): Color {
    val inverse = 1f - ratio
    return Color(
        red = red * inverse + other.red * ratio,
        green = green * inverse + other.green * ratio,
        blue = blue * inverse + other.blue * ratio,
        alpha = alpha,
    )
}

private fun palettePrimary(themeColor: String): Color = when (themeColor) {
    "SAKURA" -> Color(0xFFFF9CA8)
    "MATERIAL_RED" -> Color(0xFFF44336)
    "MATERIAL_PINK" -> Color(0xFFE91E63)
    "MATERIAL_PURPLE" -> Color(0xFF9C27B0)
    "MATERIAL_DEEP_PURPLE" -> Color(0xFF673AB7)
    "MATERIAL_INDIGO" -> Color(0xFF3F51B5)
    "MATERIAL_BLUE" -> Color(0xFF2196F3)
    "MATERIAL_LIGHT_BLUE" -> Color(0xFF03A9F4)
    "MATERIAL_CYAN" -> Color(0xFF00BCD4)
    "MATERIAL_TEAL" -> Color(0xFF009688)
    "MATERIAL_GREEN" -> Color(0xFF4FAF50)
    "MATERIAL_LIGHT_GREEN" -> Color(0xFF8BC3A4)
    "MATERIAL_LIME" -> Color(0xFFCDDC39)
    "MATERIAL_YELLOW" -> Color(0xFFFFEB3B)
    "MATERIAL_AMBER" -> Color(0xFFFFC107)
    "MATERIAL_ORANGE" -> Color(0xFFFF9800)
    "MATERIAL_DEEP_ORANGE" -> Color(0xFFFF5722)
    "MATERIAL_BROWN" -> Color(0xFF795548)
    "MATERIAL_BLUE_GREY" -> Color(0xFF607D8F)
    else -> CatppuccinBlue
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun LSPosedComposeTheme(
    darkTheme: Boolean,
    followSystemAccent: Boolean,
    blackDarkTheme: Boolean,
    themeColor: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val customDark = darkColorScheme(
        primary = palettePrimary(themeColor),
        secondary = CatppuccinSky,
        tertiary = CatppuccinPink,
    )

    val customLight = lightColorScheme(
        primary = palettePrimary(themeColor),
        secondary = CatppuccinLavender,
        tertiary = CatppuccinGreen,
    )

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> customDark
        else -> customLight
    }.let { scheme ->
        if (blackDarkTheme && darkTheme) scheme.amoled() else scheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

private fun ColorScheme.amoled(): ColorScheme = copy(
    background = AmoledBlack,
    surface = AmoledBlack,
    surfaceVariant = DarkGrey.blend(AmoledBlack, 0.8f),
    surfaceContainer = DarkGrey.blend(AmoledBlack, 0.8f),
    surfaceContainerLow = DarkGrey.blend(AmoledBlack, 0.8f),
    surfaceContainerLowest = DarkGrey.blend(AmoledBlack, 0.8f),
    surfaceContainerHigh = DarkGrey.blend(AmoledBlack, 0.8f),
    surfaceContainerHighest = DarkGrey.blend(AmoledBlack, 0.8f),
)

@Composable
fun rememberDarkThemeFromPreference(mode: String): Boolean {
    return when (mode) {
        "MODE_NIGHT_YES" -> true
        "MODE_NIGHT_NO" -> false
        else -> isSystemInDarkTheme()
    }
}
