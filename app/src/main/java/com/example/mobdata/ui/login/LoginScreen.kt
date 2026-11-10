package com.example.mobdata.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobdata.data.Usuario
import com.example.mobdata.ui.componentes.BotonPrincipal
import com.example.mobdata.ui.componentes.BotonSecundario
import com.example.mobdata.ui.componentes.CampoFormulario
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.Paper
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
import com.example.mobdata.viewmodel.LoginViewModel

// ==========================================
// PANTALLA: acceso local con alias + contraseña (RF01).
// Dos modos en la misma pantalla: INICIAR SESIÓN y CREAR CUENTA.
// Toda la validación y el acceso a Room viven en LoginViewModel.
// ==========================================
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (Usuario) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var pestana by rememberSaveable { mutableStateOf(0) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Paper)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(36.dp))

            // Marca de la app: logo cuadrado verde + nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(Teal, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "MD",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("MobData", color = Spruce, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Autorregistro · Bienestar",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Text("Acceso al sistema", color = Spruce, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(6.dp))
            Text(
                "Ingresa con tus credenciales locales.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(20.dp))

            // Tarjeta blanca que agrupa credenciales y ayuda
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Divider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text("Credenciales", style = MaterialTheme.typography.titleLarge, color = Spruce)
                    Spacer(Modifier.height(16.dp))

                    // Selector de modo: 0 = iniciar sesión, 1 = crear cuenta
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SelectorModo(
                            texto = "Iniciar sesión",
                            activo = pestana == 0,
                            onClick = { pestana = 0; viewModel.cambiarModo(esRegistro = false) }
                        )
                        SelectorModo(
                            texto = "Crear cuenta",
                            activo = pestana == 1,
                            onClick = { pestana = 1; viewModel.cambiarModo(esRegistro = true) }
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    CampoFormulario(
                        valor = estado.alias,
                        onValorChange = viewModel::actualizarAlias,
                        etiqueta = "Alias ficticio",
                        placeholder = "Ej.: luna.azul",
                        opcionesTeclado = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoFormulario(
                        valor = estado.password,
                        onValorChange = viewModel::actualizarPassword,
                        etiqueta = "Contraseña",
                        placeholder = "Ingresa tu contraseña",
                        visualTransformacion = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        iconoFinal = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = TextSecondary
                                )
                            }
                        },
                        opcionesTeclado = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (estado.esRegistro) ImeAction.Next else ImeAction.Done
                        )
                    )

                    if (estado.esRegistro) {
                        Spacer(Modifier.height(12.dp))
                        CampoFormulario(
                            valor = estado.confirmacionPassword,
                            onValorChange = viewModel::actualizarConfirmacionPassword,
                            etiqueta = "Repite la contraseña",
                            placeholder = "Vuelve a escribir la contraseña",
                            visualTransformacion = PasswordVisualTransformation(),
                            opcionesTeclado = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )
                    }

                    // Mensaje de error único (validación o fallo de autenticación)
                    estado.mensajeError?.let { error ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            error,
                            color = com.example.mobdata.ui.theme.Error,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                    BotonPrincipal(
                        texto = if (estado.esRegistro) "Crear cuenta" else "Ingresar al sistema",
                        onClick = { viewModel.enviar(onLoginSuccess) },
                        habilitado = !estado.cargando,
                        cargando = estado.cargando,
                        modifier = Modifier.height(54.dp)
                    )

                    Spacer(Modifier.height(14.dp))
                    Text(
                        if (estado.esRegistro) {
                            "Tu cuenta y tus registros quedan solo en este teléfono."
                        } else {
                            "¿Primera vez? Crea una cuenta local con un alias ficticio."
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(12.dp))

                    if (!estado.esRegistro) {
                        TextButton(
                            onClick = { pestana = 1; viewModel.cambiarModo(esRegistro = true) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                "¿No tienes cuenta? Crear una",
                                color = Teal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        TextButton(
                            onClick = { pestana = 0; viewModel.cambiarModo(esRegistro = false) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                "¿Ya tienes cuenta? Iniciar sesión",
                                color = Teal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(12.dp))

                    // RNF04: declaración de alcance visible antes de ingresar
                    Text(
                        "Alcance del prototipo",
                        color = Spruce,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Prototipo académico de autorregistro. No entrega diagnósticos ni " +
                            "tratamientos, no reemplaza a un profesional de la salud y no es " +
                            "un servicio de urgencias. Usa solo datos ficticios.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// Selector de modo (segmentado, dentro de la tarjeta de credenciales)
@Composable
private fun SelectorModo(texto: String, activo: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            containerColor = if (activo) Teal else Surface,
            contentColor = if (activo) Surface else TextSecondary
        ),
        border = if (activo) null else BorderStroke(1.dp, Divider),
        modifier = Modifier.height(40.dp)
    ) {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
