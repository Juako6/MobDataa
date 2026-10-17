package com.example.mobdata.ui.dbt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.ui.componentes.BannerExito
import com.example.mobdata.ui.componentes.BotonGuardar
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.componentes.MensajeError
import com.example.mobdata.ui.componentes.SelectorEscala
import com.example.mobdata.ui.componentes.SelectorOpciones
import com.example.mobdata.ui.componentes.rememberNotificadorRegistro
import com.example.mobdata.viewmodel.DbtViewModel

// ==========================================
// PANTALLA (RF03): diario de habilidades DBT (crear / editar)
// La habilidad se selecciona desde el catálogo almacenado en Room.
// ==========================================
@Composable
fun FormularioDbtScreen(
    viewModel: DbtViewModel,
    registroId: Long,
    onVolver: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val estado by viewModel.estadoFormulario.collectAsState()
    val habilidades by viewModel.habilidades.collectAsState()
    val haptic = LocalHapticFeedback.current
    val notificar = rememberNotificadorRegistro()

    LaunchedEffect(registroId) {
        if (registroId == 0L) viewModel.iniciarNuevo() else viewModel.cargarParaEditar(registroId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoPantalla(
            titulo = if (estado.modoEdicion) "Editar registro DBT" else "Registrar habilidad DBT",
            onVolver = onVolver
        )

        if (!estado.modoEdicion) {
            BannerExito(
                visible = estado.guardadoExitoso,
                onOcultar = { viewModel.ocultarBannerConfirmacion() },
                onVerHistorial = onVerHistorial
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Habilidad del catálogo, agrupada por categoría
        Text("1. Habilidad practicada *", fontWeight = FontWeight.Medium)
        if (habilidades.isEmpty()) {
            Text(
                "Cargando catálogo…",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            habilidades.groupBy { it.categoria }.forEach { (categoria, lista) ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(categoria, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                SelectorOpciones(
                    titulo = "",
                    opciones = lista.map { it.titulo },
                    seleccion = estado.habilidad,
                    onSeleccion = { titulo ->
                        lista.find { it.titulo == titulo }?.let(viewModel::seleccionarHabilidad)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = estado.situacion,
            onValueChange = viewModel::actualizarSituacion,
            label = { Text("2. Emoción o situación asociada *") },
            supportingText = { Text("Ej.: discusión en el trabajo, mucho ruido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorEscala(
            titulo = "3. Nivel de intensidad *",
            valor = estado.intensidad,
            onValor = viewModel::seleccionarIntensidad,
            ayuda = "1 = muy baja · 5 = muy alta"
        )

        Spacer(modifier = Modifier.height(8.dp))

        SelectorEscala(
            titulo = "4. Utilidad percibida *",
            valor = estado.utilidad,
            onValor = viewModel::seleccionarUtilidad,
            ayuda = "1 = nada útil · 5 = muy útil"
        )

        Text(
            "Los campos con * son obligatorios. Usa solo datos ficticios.",
            style = MaterialTheme.typography.bodySmall
        )

        MensajeError(estado.mensajeError)

        BotonGuardar(modoEdicion = estado.modoEdicion) {
            val eraEdicion = estado.modoEdicion
            viewModel.guardarRegistro(
                onExito = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (eraEdicion) onVolver() else notificar()
                }
            )
        }
    }
}
