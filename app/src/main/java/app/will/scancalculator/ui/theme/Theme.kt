package app.will.scancalculator.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import app.will.scancalculator.Const
import com.will.scancalculator.BuildConfig

private val RedColorScheme = lightColorScheme(
    primary = RedPrimary, secondary = RedSecondary, tertiary = RedTertiary
)

private val GreenColorScheme = lightColorScheme(
    primary = GreenPrimary, secondary = GreenSecondary, tertiary = GreenTertiary

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ScanCalculatorTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = when (BuildConfig.BUILD_THEME) {
        Const.BuildTheme.RED.name -> RedColorScheme
        Const.BuildTheme.GREEN.name -> GreenColorScheme
        else -> GreenColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                BuildConfig.BUILD_THEME == Const.BuildTheme.GREEN.name
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}