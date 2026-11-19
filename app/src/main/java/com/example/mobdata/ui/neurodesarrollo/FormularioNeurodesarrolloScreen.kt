package com.example.mobdata.ui.neurodesarrollo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.mobdata.model.OPCIONES_EPISODIO
import com.example.mobdata.ui.componentes.BannerExito
import com.example.mobdata.ui.componentes.BotonGuardar
import com.example.mobdata.ui.componentes.CampoFormulario
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.componentes.MensajeError
import com.example.mobdata.ui.componentes.SelectorEscala
import com.example.mobdata.ui.componentes.SelectorOpciones
import com.example.mobdata.ui.componentes.rememberNotificadorRegistro
import com.example.mobdata.ui.theme.Paper
import com.example.mobdata.ui.theme.TextSecondary
import com.example.mobdata.viewmodel.NeurodesarrolloViewModel

// ==========================================
// PANTALLA (RF02): registro de Neurodesarrollo (crear / editar)
// ==========================================
@Composable
fun FormularioNeurodesarrolloScreen(
    viewModel: NeurodesarrolloViewModel,
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
            .background(Paper)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoPantalla(
            titulo = if (estado.modoEdicion) "Editar registro de neurodesarrollo"
            else "Registrar neurodesarrollo",
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

        CampoFormulario(
            valor = estado.rutina,
            onValorChange = viewModel::actualizarRutina,
            etiqueta = "1. Rutina del día *",
            ayuda = "Ej.: Rutina de mañana, estudio, descanso"
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoFormulario(
            valor = estado.organizacionTareas,
            onValorChange = viewModel::actualizarOrganizacion,
            etiqueta = "2. Organización de tareas (opcional)",
            ayuda = "Cómo dividiste o ordenaste lo pendiente",
            singleLine = false,
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorOpciones(
            titulo = "3. Episodio *",
            opciones = OPCIONES_EPISODIO,
            seleccion = estado.tipoEpisodio,
            onSeleccion = viewModel::seleccionarEpisodio
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoFormulario(
            valor = estado.estrategia,
            onValorChange = viewModel::actualizarEstrategia,
            etiqueta = "4. Estrategia utilizada *",
            ayuda = "Ej.: pausa de 5 minutos, bajar el ruido, lista corta",
            singleLine = false,
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorEscala(
            titulo = "5. Resultado percibido *",
            valor = estado.resultado,
            onValor = viewModel::seleccionarResultado,
            ayuda = "1 = poco favorable · 5 = muy favorable"
        )

        Text(
            "Los campos con * son obligatorios. Usa solo datos ficticios.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
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
