package com.example.mobdata.ui.animo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.mobdata.ui.componentes.BannerExito
import com.example.mobdata.ui.componentes.BotonGuardar
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.componentes.MensajeError
import com.example.mobdata.ui.componentes.SelectorEscala
import com.example.mobdata.ui.componentes.SelectorOpciones
import com.example.mobdata.ui.componentes.rememberNotificadorRegistro
import com.example.mobdata.viewmodel.AnimoViewModel

private val EMOCIONES = listOf("Calma 😊", "Tristeza 😔", "Ansiedad 😰", "Enojo 😠", "Alegría 😄")

// ==========================================
// PANTALLA: Formulario de estado de ánimo (crear / editar)
// registroId = 0 -> registro nuevo | registroId > 0 -> editar ese registro
// ==========================================
@Composable
fun FormularioAnimoScreen(
    viewModel: AnimoViewModel,
    registroId: Long,
    onVolver: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val estado by viewModel.estadoFormulario.collectAsState()
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
            titulo = if (estado.modoEdicion) "Editar estado de ánimo" else "Registrar estado de ánimo",
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

        SelectorOpciones(
            titulo = "1. Selecciona tu emoción:",
            opciones = EMOCIONES,
            seleccion = estado.emocionSeleccionada,
            onSeleccion = viewModel::seleccionarEmocion
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorEscala(
            titulo = "2. Nivel de intensidad (1 al 5):",
            valor = estado.intensidad,
            onValor = viewModel::seleccionarIntensidad
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.notaTexto,
            onValueChange = viewModel::actualizarNota,
            label = { Text("3. Nota o contexto (opcional)") },
            supportingText = { Text("Usa solo datos ficticios.") },
            modifier = Modifier.fillMaxWidth()
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
