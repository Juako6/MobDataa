package com.example.mobdata.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*
 * Esquema de colores MobData.
 *
 * Mapea la paleta semántica a los roles de Material 3 para que los
 * componentes estándar (Button, TopAppBar, Scaffold, etc.) consuman los
 * colores correctos sin valores literales en las pantallas.
 *
 * El color dinámico se ignora a propósito: la identidad es fija para
 * mantener consistencia visual en todos los dispositivos.
 */
private val EsquemaOscuro = darkColorScheme(
    primary = Teal,
    onPrimary = Surface,
    primaryContainer = SoftSage,
    onPrimaryContainer = Spruce,
    secondary = Success,
    onSecondary = Surface,
    tertiary = Accent,
    onTertiary = Spruce,
    background = SpruceOscuro,
    onBackground = Surface,
    surface = SurfaceOscura,
    onSurface = OnSurfaceOscura,
    surfaceVariant = SurfaceVarianteOscura,
    onSurfaceVariant = OnSurfaceVarianteOscura,
    outline = Divider,
    error = Error,
    onError = Surface
)

private val EsquemaClaro = lightColorScheme(
    primary = Teal,                       // acción primaria
    onPrimary = Surface,                  // texto sobre acción primaria
    primaryContainer = SoftSage,          // contenedor de acento suave
    onPrimaryContainer = Spruce,
    secondary = Success,                  // estados de éxito
    onSecondary = Surface,
    tertiary = Accent,                    // acento secundario (ámbar)
    onTertiary = Spruce,
    background = Paper,                   // fondo general de pantalla
    onBackground = Spruce,                // texto sobre fondo
    surface = Surface,                    // formularios, paneles, contenido
    onSurface = Spruce,                   // texto sobre superficie
    surfaceVariant = SoftSage,            // variantes de superficie
    onSurfaceVariant = TextSecondary,
    outline = Divider,                    // separadores y contornos
    error = Error,                        // errores y rechazo
    onError = Surface,
    inversePrimary = Spruce,
    inverseSurface = Spruce,
    inverseOnSurface = Surface
)

@Composable
fun MobDataTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
