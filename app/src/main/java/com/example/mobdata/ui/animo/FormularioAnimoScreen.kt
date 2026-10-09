package com.example.mobdata.ui.animo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.viewmodel.AnimoViewModel

// ==========================================
// PANTALLA: Formulario de check-in de ánimo (RF02)
// Recibe acciones -> envía eventos al ViewModel -> muestra estado
// ==========================================
@Composable
fun FormularioAnimoScreen(viewModel: AnimoViewModel) {
    val estado by viewModel.estadoFormulario.collectAsState()

    // RECURSO NATIVO: feedback háptico del hardware
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Check-in de Estado de Ánimo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // MENSAJE DE ÉXITO (retroalimentación visible)
        AnimatedVisibility(
            visible = estado.guardadoExitoso,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("✅ Registro guardado localmente", modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.ocultarBannerConfirmacion() }) {
                        Text("OK")
                    }
                }
            }
        }

        // 1. Selección de emoción
        Text("1. Selecciona tu emoción:", fontWeight = FontWeight.Medium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val emociones = listOf("Calma 😊", "Tristeza 😔", "Ansiedad 😰")
            emociones.forEach { opcion ->
                FilterChip(
                    selected = estado.emocionSeleccionada == opcion,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.seleccionarEmocion(opcion)
                    },
                    label = { Text(opcion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Nivel de intensidad
        Text("2. Nivel de intensidad (1 al 5):", fontWeight = FontWeight.Medium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            (1..5).forEach { nivel ->
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.seleccionarIntensidad(nivel)
                    },
                    colors = if (estado.intensidad == nivel) {
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

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Nota opcional
        OutlinedTextField(
            value = estado.notaTexto,
            onValueChange = { viewModel.actualizarNota(it) },
            label = { Text("Nota o contexto opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        // MENSAJE DE ERROR (retroalimentación visible de la validación)
        estado.mensajeError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.guardarRegistro(
                    onExito = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("GUARDAR EN EL TELÉFONO")
        }
    }
}
