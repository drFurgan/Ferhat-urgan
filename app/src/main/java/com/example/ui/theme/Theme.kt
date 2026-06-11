package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = ProfessionalIndigoDark,
    secondary = AccentGoldDark,
    tertiary = Color(0xFF38BDF8), // Light Sky Blue highlight
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = OutlineDark,
    primaryContainer = ProfessionalIndigoDark.copy(alpha = 0.15f),
    onPrimaryContainer = ProfessionalIndigoDark,
    surfaceVariant = OutlineDark,
    onSurfaceVariant = TextSecondaryDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ProfessionalIndigoLight,
    secondary = AccentGoldLight,
    tertiary = Color(0xFF0369A1), // Sky Blue Dark
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = OutlineLight,
    primaryContainer = ProfessionalIndigoLight.copy(alpha = 0.08f),
    onPrimaryContainer = ProfessionalIndigoLight,
    surfaceVariant = OutlineLight.copy(alpha = 0.4f),
    onSurfaceVariant = TextSecondaryLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Setting default to false to force our gorgeous brand identity
  dynamicColor: Boolean = false,
  customPrimary: Color? = null,
  customSecondary: Color? = null,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      customPrimary != null && customSecondary != null -> {
        if (darkTheme) {
          darkColorScheme(
            primary = customPrimary,
            secondary = customSecondary,
            tertiary = customSecondary,
            background = BackgroundDark,
            surface = SurfaceDark,
            onBackground = TextPrimaryDark,
            onSurface = TextPrimaryDark,
            outline = OutlineDark,
            primaryContainer = customPrimary.copy(alpha = 0.2f),
            onPrimaryContainer = customPrimary,
            surfaceVariant = OutlineDark,
            onSurfaceVariant = TextSecondaryDark
          )
        } else {
          lightColorScheme(
            primary = customPrimary,
            secondary = customSecondary,
            tertiary = customSecondary,
            background = BackgroundLight,
            surface = SurfaceLight,
            onBackground = TextPrimaryLight,
            onSurface = TextPrimaryLight,
            outline = OutlineLight,
            primaryContainer = customPrimary.copy(alpha = 0.1f),
            onPrimaryContainer = customPrimary,
            surfaceVariant = OutlineLight.copy(alpha = 0.4f),
            onSurfaceVariant = TextSecondaryLight
          )
        }
      }

      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
