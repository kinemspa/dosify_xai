package com.xai.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.ExperimentalMaterial3Api
import android.app.Activity

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentPurple,
    background = BackgroundWhite,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentPurple,
    background = TextBlack,
    onPrimary = BackgroundWhite,
    onSecondary = BackgroundWhite,
    error = ErrorRed
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val windowSize: WindowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity) // Cast to Activity; use in screens e.g., if (windowSize.widthSizeClass == WindowWidthSizeClass.Expanded) { /* tablet */ }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}