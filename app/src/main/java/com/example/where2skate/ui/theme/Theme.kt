package com.example.where2skate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- Light Color Scheme ---
private val SkaterLightColorScheme = lightColorScheme(
    primary = SkaterYellowLight,            // Color primario (botones principales, FABs, elementos activos)
    onPrimary = SkaterBlack,                // Color del texto/iconos sobre el primario
    primaryContainer = SkaterYellowLight,   // Contenedores que usan el color primario (menos énfasis)
    onPrimaryContainer = SkaterBlack,       // Texto/iconos sobre primaryContainer

    secondary = SkaterMediumGrayLight,      // Color secundario (filtros, elementos menos prominentes)
    onSecondary = SkaterWhite,              // Texto/iconos sobre el secundario
    secondaryContainer = SkaterLightGrayLight, // Contenedores secundarios
    onSecondaryContainer = SkaterDarkGrayLight, // Texto/iconos sobre secondaryContainer

    tertiary = SkaterDarkGrayLight,         // Color terciario (acento para elementos decorativos o menos importantes)
    onTertiary = SkaterWhite,               // Texto/iconos sobre el terciario
    tertiaryContainer = SkaterMediumGrayLight,
    onTertiaryContainer = SkaterWhite,

    error = ErrorRed,                       // Color para errores
    onError = OnErrorRed,                   // Texto/iconos sobre el color de error
    errorContainer = Color(0xFFFCD8DF),    // Contenedor para errores
    onErrorContainer = Color(0xFF410002),  // Texto/iconos sobre errorContainer

    background = SkaterLightGrayLight,      // Color de fondo principal de las pantallas
    onBackground = SkaterDarkGrayLight,     // Color del texto/iconos sobre el fondo principal

    surface = SkaterWhite,                  // Color de las superficies de componentes (Cards, Sheets, Menus)
    onSurface = SkaterDarkGrayLight,        // Texto/iconos sobre estas superficies

    surfaceVariant = SkaterLightGrayLight,  // Variante de superficie (para delinear o diferenciar secciones)
    onSurfaceVariant = SkaterMediumGrayLight, // Texto/iconos sobre surfaceVariant

    outline = SkaterMediumGrayLight,        // Bordes y divisores
    outlineVariant = SkaterLightGrayLight,  // Divisores sutiles

    // scrim = Color.Black.copy(alpha = 0.32f) // Usado para oscurecer fondos detrás de diálogos, etc.
    // inverseSurface, inverseOnSurface, inversePrimary son para escenarios de temas inversos (raro)
)

// --- Dark Color Scheme ---
private val SkaterDarkColorScheme = darkColorScheme(
    primary = SkaterYellowDark,
    onPrimary = SkaterBlack, // El amarillo oscuro aún puede llevar negro encima para buen contraste
    primaryContainer = SkaterYellowDark,
    onPrimaryContainer = SkaterBlack,

    secondary = SkaterMediumGrayDark,
    onSecondary = SkaterLightGrayDark,
    secondaryContainer = SkaterDarkGrayLight, // Usamos un gris más oscuro como base
    onSecondaryContainer = SkaterLightGrayDark,

    tertiary = SkaterLightGrayDark,
    onTertiary = SkaterDarkGrayLight,
    tertiaryContainer = SkaterMediumGrayDark,
    onTertiaryContainer = SkaterLightGrayDark,

    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = SkaterDarkBackground, // Un fondo casi negro
    onBackground = SkaterLightGrayDark,   // Texto claro sobre fondo oscuro

    surface = Color(0xFF1E1E1E), // Superficies un poco más claras que el fondo
    onSurface = SkaterLightGrayDark,

    surfaceVariant = Color(0xFF2C2C2C), // Variante de superficie un poco más clara
    onSurfaceVariant = SkaterMediumGrayDark,

    outline = SkaterMediumGrayDark,
    outlineVariant = Color(0xFF383838),
)

@Composable
fun Where2SkateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color no lo usaremos para forzar nuestra temática skater
    dynamicColor: Boolean = false, // <--- CAMBIADO A FALSE
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SkaterDarkColorScheme // Usamos nuestro Dark Scheme
        else -> SkaterLightColorScheme    // Usamos nuestro Light Scheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Tu archivo Type.kt no necesita cambios por ahora
        content = content
    )
}