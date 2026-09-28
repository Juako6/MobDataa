package com.example.mobdata

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ControladorDeAcceso()
                }
            }
        }
    }
}

@Composable
fun ControladorDeAcceso() {
    val context = LocalContext.current
    // Acceso al almacenamiento local de preferencias
    val sharedPref = remember { context.getSharedPreferences("MobData", Context.MODE_PRIVATE) }

    // Leemos si ya existe un alias guardado
    var aliasGuardado by remember {
        mutableStateOf(sharedPref.getString("alias_usuario", "") ?: "")
    }

    if (aliasGuardado.isEmpty()) {
        // PANTALLA 1: RF01 - Autenticación Sintética y Consentimiento
        PantallaLoginSintetico(
            onIngresar = { nuevoAlias, notificaciones ->
                // Guardamos en el almacenamiento local
                sharedPref.edit()
                    .putString("alias_usuario", nuevoAlias)
                    .putBoolean("notificaciones_discretas", notificaciones)
                    .apply()
                // Actualizamos el estado para cambiar de pantalla
                aliasGuardado = nuevoAlias
            }
        )
    } else {
        // PANTALLA 2: Dashboard Principal
        PantallaPrincipal(
            aliasUsuario = aliasGuardado,
            onCerrarSesion = {
                // Borramos las preferencias guardadas
                sharedPref.edit().clear().apply()
                aliasGuardado = ""
            }
        )
    }
}

@Composable
fun PantallaLoginSintetico(onIngresar: (String, Boolean) -> Unit) {
    var aliasInput by remember { mutableStateOf("Usuario_Sintetico_01") }
    var activarNotificaciones by remember { mutableStateOf(true) }
    var errorTexto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MobData",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )


        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de Descargo Legal / Privacidad (Requerimiento Académico)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚠️ Aviso Legal y Privacidad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Este es un MVP académico de autorregistro. Funciona 100% con datos sintéticos. No emite diagnósticos, no receta tratamientos ni sustituye la atención profesional ni de urgencia.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para ingresar el Alias Ficticio
        OutlinedTextField(
            value = aliasInput,
            onValueChange = {
                aliasInput = it
                if (it.isNotEmpty()) errorTexto = ""
            },
            label = { Text("Alias o Identificador Ficticio") },
            placeholder = { Text("Ej: Usuario_01") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorTexto.isNotEmpty()) {
            Text(
                text = errorTexto,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch de Preferencias (Recordatorios discretos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Recordatorios discretos", fontWeight = FontWeight.Medium)
                Text(
                    text = "Notificaciones locales sin texto sensible",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = activarNotificaciones,
                onCheckedChange = { activarNotificaciones = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Ingreso
        Button(
            onClick = {
                if (aliasInput.trim().isEmpty()) {
                    errorTexto = "Debes ingresar un alias ficticio para continuar."
                } else {
                    onIngresar(aliasInput.trim(), activarNotificaciones)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("INGRESAR A LA APLICACIÓN", fontSize = 16.sp)
        }
    }
}

@Composable
fun PantallaPrincipal(aliasUsuario: String, onCerrarSesion: () -> Unit) {
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    label = { Text("Ánimo") },
                    icon = { Text("") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    label = { Text("Neuro") },
                    icon = { Text("") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 2,
                    onClick = { pestanaSeleccionada = 2 },
                    label = { Text("DBT") },
                    icon = { Text("") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 3,
                    onClick = { pestanaSeleccionada = 3 },
                    label = { Text("Adicciones") },
                    icon = { Text("") }
                )
            }
        }
    ) { espacioPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacioPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Encabezado con información del perfil autenticado (RF01)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Perfil Ficticio Activo:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = aliasUsuario,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(onClick = onCerrarSesion) {
                            Text("Salir / Cambiar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contenido dinámico según la pestaña
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (pestanaSeleccionada) {
                        0 -> Text("Módulo: Estado de Ánimo\n(Siguiente paso: Formulario)", textAlign = TextAlign.Center)
                        1 -> Text("Módulo: Neurodesarrollo\n(Siguiente paso: Formulario)", textAlign = TextAlign.Center)
                        2 -> Text("Módulo: Habilidades DBT\n(Siguiente paso: Formulario)", textAlign = TextAlign.Center)
                        3 -> Text("Módulo: Adicciones\n(Siguiente paso: Formulario)", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}