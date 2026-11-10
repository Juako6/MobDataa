package com.example.mobdata.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Sistema visual MobData — inspirado en el lenguaje institucional de salud
 * (CMQ Salud): verde profundo, papel crema y superficies blancas limpias.
 *
 * Los colores se centralizan como roles semánticos; las pantallas deben
 * consumirlos por su nombre semántico, no por valores literales.
 *
 * Paleta de referencia:
 *   Tinta spruce   #173F38  → texto principal, títulos y marca
 *   Teal de acción #1D6B5A  → acción primaria, selección y enlaces
 *   Fondo papel    #F5F7F4  → fondo general de pantalla
 *   Superficie     #FFFFFF  → formularios, paneles y contenido
 *   Sage suave     #E8F0EB  → selección, foco suave y agrupaciones
 *   Texto secund.  #536A62  → ayuda, descripción y metadatos
 *   Borde          #D5E0DA  → separadores y contornos de control
 *   Atención       #82580A / #FFF1CE → avisos y advertencias
 *   Error          #A8343F / #FDEBEC → errores y rechazo
 *   Éxito          #216A49 / #E7F3EB → guardado y confirmación
 */

// --- Tinta principal / marca ---
val Spruce = Color(0xFF173F38)            // texto principal, títulos, marca

// --- Color de acción ---
val Teal = Color(0xFF1D6B5A)              // acción primaria, selección, enlaces

// --- Fondos ---
val Paper = Color(0xFFF5F7F4)             // fondo general de pantalla
val Surface = Color(0xFFFFFFFF)           // formularios, paneles, contenido

// --- Acento suave ---
val SoftSage = Color(0xFFE8F0EB)          // selección, foco suave, agrupaciones

// --- Texto ---
val TextSecondary = Color(0xFF536A62)     // ayuda, descripción, metadatos

// --- Contornos ---
val Divider = Color(0xFFD5E0DA)           // separadores y contornos de control

// --- Atención (advertencias) ---
val AttentionInk = Color(0xFF82580A)
val AttentionBg = Color(0xFFFFF1CE)

// --- Error / rechazo ---
val Error = Color(0xFFA8343F)
val ErrorBg = Color(0xFFFDEBEC)

// --- Éxito / confirmación ---
val Success = Color(0xFF216A49)
val SuccessBg = Color(0xFFE7F3EB)

// --- Acento secundario ---
val Accent = Color(0xFFE6B15B)

// --- Superficies oscuras (modo noche) ---
val SpruceOscuro = Color(0xFF0E1513)
val SurfaceOscura = Color(0xFF1D4544)
val OnSurfaceOscura = Color(0xFFE6F2EE)
val SurfaceVarianteOscura = Color(0xFF4A5D57)
val OnSurfaceVarianteOscura = Color(0xFFC2D2CC)
