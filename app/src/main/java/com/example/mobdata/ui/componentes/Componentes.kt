package com.example.mobdata.ui.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mobdata.util.Notificaciones

// ==========================================
// COMPONENTES REUTILIZABLES de los formularios y el historial
// ==========================================

// Encabezado con botón de retorno
@Composable
fun EncabezadoPantalla(titulo: String, onVolver: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onVolver) { Text("← Volver") }
    }
    Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
}

// Selector de una opción entre varias (chips)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorOpciones(
    titulo: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    if (titulo.isNotEmpty()) {
        Text(titulo, fontWeight = FontWeight.Medium)
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        opciones.forEach { opcion ->
            FilterChip(
                selected = seleccion == opcion,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSeleccion(opcion)
                },
                label = { Text(opcion) }
            )
        }
    }
}

// Escala de 1 a 5
@Composable
fun SelectorEscala(
    titulo: String,
    valor: Int,
    onValor: (Int) -> Unit,
    ayuda: String? = null
) {
    val haptic = LocalHapticFeedback.current
    Text(titulo, fontWeight = FontWeight.Medium)
    if (ayuda != null) {
        Text(ayuda, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        (1..5).forEach { nivel ->
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onValor(nivel)
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(0.dp),
                colors = if (valor == nivel) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text(nivel.toString())
            }
        }
    }
}

// Mensaje de error de validación (en rojo)
@Composable
fun MensajeError(error: String?) {
    if (error != null) {
        Text(
            error,
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Banner animado de éxito (aparece solo cuando el registro se guardó)
@Composable
fun BannerExito(
    visible: Boolean,
    onOcultar: () -> Unit,
    onVerHistorial: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✅ Registro guardado exitosamente")
                Row {
                    if (onVerHistorial != null) {
                        TextButton(onClick = onVerHistorial) { Text("VER EN HISTORIAL") }
                    }
                    TextButton(onClick = onOcultar) { Text("OK") }
                }
            }
        }
    }
}

// Botón principal de guardado
@Composable
fun BotonGuardar(modoEdicion: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp)
    ) {
        Text(if (modoEdicion) "ACTUALIZAR REGISTRO" else "GUARDAR EN EL TELÉFONO")
    }
}

// Estado de sincronización del registro (RNF01)
@Composable
fun EtiquetaSincronizacion(sincronizado: Boolean) {
    Text(
        if (sincronizado) "☁️ Sincronizado" else "📱 Guardado solo en el teléfono (pendiente de sincronizar)",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

// Recurso nativo: notificación neutral al guardar. Solicita el permiso en runtime
// (Android 13+) y devuelve la acción que se debe invocar al guardar.
@Composable
fun rememberNotificadorRegistro(): () -> Unit {
    val contexto = LocalContext.current
    val permiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) Notificaciones.confirmarRegistro(contexto)
    }
    return remember(contexto, permiso) {
        {
            val permisoConcedido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    contexto, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            if (permisoConcedido) {
                Notificaciones.confirmarRegistro(contexto)
            } else {
                permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
