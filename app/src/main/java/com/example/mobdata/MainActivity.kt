package com.example.mobdata

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==========================================
// 1. MODELO DE DATOS Y ESTADO (Estado Local)
// ==========================================
data class RegistroAnimo(
    val id: Long = System.currentTimeMillis(),
    val emocion: String,
    val intensidad: Int,
    val nota: String,
    val fechaHora: String
)

data class EstadoFormularioAnimo(
    val emocionSeleccionada: String = "",
    val intensidad: Int = 0,
    val notaTexto: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false
)

// ==========================================
// 2. VIEWMODEL (Lógica de Negocio y Estado)
// ==========================================
class AnimoViewModel : ViewModel() {
    private val _estadoFormulario = MutableStateFlow(EstadoFormularioAnimo())
    val estadoFormulario: StateFlow<EstadoFormularioAnimo> = _estadoFormulario.asStateFlow()

    private val _historialRegistros = MutableStateFlow<List<RegistroAnimo>>(emptyList())
    val historialRegistros: StateFlow<List<RegistroAnimo>> = _historialRegistros.asStateFlow()

    fun seleccionarEmocion(emocion: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            emocionSeleccionada = emocion,
            mensajeError = null
        )
    }

    fun seleccionarIntensidad(nivel: Int) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            intensidad = nivel,
            mensajeError = null
        )
    }

    fun actualizarNota(texto: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(notaTexto = texto)
    }

    // VALIDACIÓN Y PERSISTENCIA
    fun guardarRegistro(onExito: () -> Unit) {
        val estadoActual = _estadoFormulario.value

        // REGLA DE VALIDACIÓN 1: Debe seleccionar una emoción
        if (estadoActual.emocionSeleccionada.isEmpty()) {
            _estadoFormulario.value = estadoActual.copy(mensajeError = "Debes seleccionar una emoción.")
            return
        }

        // REGLA DE VALIDACIÓN 2: Debe seleccionar un nivel de intensidad
        if (estadoActual.intensidad == 0) {
            _estadoFormulario.value = estadoActual.copy(mensajeError = "Selecciona un nivel de intensidad (1 al 5).")
            return
        }

        // Creación del nuevo registro
        val nuevoRegistro = RegistroAnimo(
            emocion = estadoActual.emocionSeleccionada,
            intensidad = estadoActual.intensidad,
            nota = estadoActual.notaTexto.ifBlank { "Sin nota" },
            fechaHora = "Hoy - " + java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        )

        // Actualización persistente en el estado de la lista
        _historialRegistros.value = listOf(nuevoRegistro) + _historialRegistros.value

        // Limpiar el formulario y activar confirmación
        _estadoFormulario.value = EstadoFormularioAnimo(guardadoExitoso = true)

        onExito()
    }

    fun ocultarBannerConfirmacion() {
        _estadoFormulario.value = _estadoFormulario.value.copy(guardadoExitoso = false)
    }
}

// ==========================================
// 3. ACTIVIDAD PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppPrincipal()
                }
            }
        }
    }
}

@Composable
fun AppPrincipal() {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("LemacPrefs", Context.MODE_PRIVATE) }
    var aliasUsuario by remember { mutableStateOf(sharedPref.getString("alias", "") ?: "") }

    if (aliasUsuario.isEmpty()) {
        PantallaLogin(onLogin = { alias ->
            sharedPref.edit().putString("alias", alias).apply()
            aliasUsuario = alias
        })
    } else {
        PantallaDashboard(alias = aliasUsuario, onLogout = {
            sharedPref.edit().clear().apply()
            aliasUsuario = ""
        })
    }
}

// ==========================================
// 4. PANTALLAS Y UI CON RECURSOS NATIVOS
// ==========================================
@Composable
fun PantallaLogin(onLogin: (String) -> Unit) {
    var inputAlias by remember { mutableStateOf("Usuario_Sintetico_01") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Lemac DataLab", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Autorregistro en Salud Mental", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = inputAlias,
            onValueChange = { inputAlias = it; errorMsg = "" },
            label = { Text("Alias Ficticio") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (inputAlias.trim().isEmpty()) {
                    errorMsg = "Ingresa un alias para continuar"
                } else {
                    onLogin(inputAlias.trim())
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("INGRESAR")
        }
    }
}

@Composable
fun PantallaDashboard(alias: String, onLogout: () -> Unit, viewModel: AnimoViewModel = viewModel()) {
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    label = { Text("Registro") },
                    icon = { Text("📝") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    label = { Text("Historial") },
                    icon = { Text("📊") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header Perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hola, $alias", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onLogout) { Text("Salir") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (pestanaSeleccionada) {
                    0 -> FormularioAnimoScreen(viewModel)
                    1 -> HistorialAnimoScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun FormularioAnimoScreen(viewModel: AnimoViewModel) {
    val estado by viewModel.estadoFormulario.collectAsState()

    // RECURSO NATIVO: Acceso al motor de vibración (Feedback Háptico del hardware)
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Check-in de Estado de Ánimo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // ANIMACIÓN DE CONFIRMACIÓN (AnimatedVisibility)
        AnimatedVisibility(
            visible = estado.guardadoExitoso,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅ Registro guardado localmente", modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.ocultarBannerConfirmacion() }) { Text("OK") }
                }
            }
        }

        // Selección de Emoción
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
                        // Recurso Nativo: Vibración sutil al presionar
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.seleccionarEmocion(opcion)
                    },
                    label = { Text(opcion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nivel de Intensidad
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
                    colors = if (estado.intensidad == nivel) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(nivel.toString())
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nota Opcional
        OutlinedTextField(
            value = estado.notaTexto,
            onValueChange = { viewModel.actualizarNota(it) },
            label = { Text("Nota o contexto opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de Error si la Validación Falla
        estado.mensajeError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar
        Button(
            onClick = {
                viewModel.guardarRegistro(
                    onExito = {
                        // RECURSO NATIVO: Vibración de confirmación
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

@Composable
fun HistorialAnimoScreen(viewModel: AnimoViewModel) {
    val registros by viewModel.historialRegistros.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Historial de Registros Guardados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (registros.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay registros guardados aún.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(registros) { reg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(reg.emocion, fontWeight = FontWeight.Bold)
                                Text("Nivel: ${reg.intensidad}/5", color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(reg.nota, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(reg.fechaHora, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}