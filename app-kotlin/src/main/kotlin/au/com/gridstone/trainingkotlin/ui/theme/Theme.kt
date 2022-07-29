package au.com.gridstone.trainingkotlin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette: Colors = darkColors(
  primary = Primary,
  primaryVariant = PrimaryDark,
  secondary = Secondary
)

private val LightColorPalette: Colors = lightColors(
  primary = Primary,
  primaryVariant = PrimaryDark,
  secondary = Secondary
)

@Composable
fun TrainingKotlinTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colors: Colors = if (darkTheme) DarkColorPalette
  else LightColorPalette

  MaterialTheme(
    colors = colors,
    typography = MaterialTheme.typography,
    shapes = Shapes,
    content = content
  )
}
