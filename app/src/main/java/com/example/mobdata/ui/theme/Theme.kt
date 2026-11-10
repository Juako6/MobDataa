package com.example.mobdata.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ==========================================
// TEMA MobData: verde-teal de confianza.
// Se desactiva el color dinámico a propósito para que la identidad
// de la app no cambie según el fondo de pantalla del teléfono.
// ==========================================

private val EsquemaClaro = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = SobreVerdePrimario,
    primaryContainer = ContenedorVerde,
    onPrimaryContainer = SobreContenedorVerde,

    secondary = TealSecundario,
    onSecondary = SobreTealSecundario,
    secondaryContainer = ContenedorTeal,
    onSecondaryContainer = SobreContenedorTeal,

    tertiary = AzulTerciario,
    onTertiary = SobreAzulTerciario,
    tertiaryContainer = ContenedorAzul,
    onTertiaryContainer = SobreContenedorAzul,

    background = FondoClaro,
    onBackground = SobreFondoClaro,
    surface = SuperficieClara,
    onSurface = SobreSuperficieClara,
    surfaceVariant = SuperficieVarianteClara,
    onSurfaceVariant = SobreSuperficieVarianteClara,
    outline = ContornoClaro,
    outlineVariant = ContornoVarianteClara,

    error = ErrorClaro,
    onError = SobreErrorClaro,
    errorContainer = ContenedorErrorClaro,
    onErrorContainer = SobreContenedorErrorClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdePrimarioClaro,
    onPrimary = VerdePrimarioOscuro,
    primaryContainer = Color0F3D36,
    onPrimaryContainer = ContenedorVerde,

    secondary = TealSecundarioClaro,
    onSecondary = Color00201C,
    secondaryContainer = Color1F4A45,
    onSecondaryContainer = ContenedorTeal,

    tertiary = AzulTerciarioClaro,
    onTertiary = Color001B3D,
    tertiaryContainer = Color234A79,
    onTertiaryContainer = ContenedorAzul,

    background = FondoOscuro,
    onBackground = SobreFondoOscuro,
    surface = SuperficieOscura,
    onSurface = SobreSuperficieOscura,
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = SobreSuperficieVarianteOscura,
    outline = ContornoOscuro,
    outlineVariant = ContornoVarianteOscura,

    error = ErrorOscuro,
    onError = SobreErrorOscuro,
    errorContainer = ContenedorErrorOscuro,
    onErrorContainer = SobreContenedorErrorOscuro
)

@Composable
fun MobDataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
