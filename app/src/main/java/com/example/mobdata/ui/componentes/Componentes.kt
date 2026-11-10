package com.example.mobdata.ui.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.Error
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
import com.example.mobdata.util.Notificaciones

// ==========================================
// COMPONENTES REUTILIZABLES de los formularios y el historial.
// Lenguaje visual institucional: tarjetas blancas, bordes suaves,
// verde profundo como color de marca y acento.
// ==========================================

// Encabezado con botón de retorno
@Composable
fun EncabezadoPantalla(titulo: String, onVolver: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onVolver,
            modifier = Modifier.size(40.dp).background(SoftSage, RoundedCornerShape(50))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Teal,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    Text(
        titulo,
        style = MaterialTheme.typography.headlineMedium,
        color = Spruce
    )
}

// Botón principal: una acción dominante por contexto, con estado de carga
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false,
    radioEsquina: Dp = 12.dp
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        colors = ButtonDefaults.buttonColors(
            containerColor = Teal,
            contentColor = Surface,
            disabledContainerColor = Teal.copy(alpha = 0.38f),
            disabledContentColor = Surface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(radioEsquina),
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                color = Surface,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(texto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Botón secundario: jerarquía claramente menor, con contorno
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    radioEsquina: Dp = 12.dp,
    icono: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Spruce,
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Divider),
        shape = RoundedCornerShape(radioEsquina),
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = Spruce, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Spruce)
    }
}

// Campo de formulario compartido: etiqueta persistente, ayuda y error
@Composable
fun CampoFormulario(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    ayuda: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 5,
    radioEsquina: Dp = 12.dp,
    visualTransformacion: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    iconoFinal: (@Composable (() -> Unit))? = null,
    opcionesTeclado: androidx.compose.foundation.text.KeyboardOptions =
        androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        isError = error != null,
        visualTransformation = visualTransformacion,
        trailingIcon = iconoFinal,
        keyboardOptions = opcionesTeclado,
        supportingText = {
            when {
                error != null -> Text(error, color = Error, fontSize = 12.sp)
                ayuda != null -> Text(ayuda, color = TextSecondary, fontSize = 12.sp)
            }
        },
        shape = RoundedCornerShape(radioEsquina),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Divider,
            focusedLabelColor = Teal,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = Spruce,
            unfocusedTextColor = Spruce,
            cursorColor = Teal,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            errorBorderColor = Error,
            errorLabelColor = Error
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// Tarjeta contenedora blanca con borde suave (agrupa formularios y secciones)
@Composable
fun TarjetaContenedora(
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            contenido()
        }
    }
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
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            color = Spruce
        )
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
                label = { Text(opcion) },
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SoftSage,
                    selectedLabelColor = Spruce,
                    containerColor = Surface,
                    labelColor = TextSecondary
                ),
                border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = seleccion == opcion,
                    borderColor = Divider,
                    selectedBorderColor = Teal,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp
                )
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
    Text(titulo, style = MaterialTheme.typography.titleMedium, color = Spruce)
    if (ayuda != null) {
        Text(ayuda, fontSize = 12.sp, color = TextSecondary)
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
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    if (valor == nivel) Teal else Divider
                ),
                colors = if (valor == nivel) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = SoftSage,
                        contentColor = Spruce
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface,
                        contentColor = TextSecondary
                    )
                }
            ) {
                Text(nivel.toString(), fontWeight = FontWeight.SemiBold)
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
            color = Error,
            fontSize = 13.sp,
            lineHeight = 18.sp,
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
        enter = fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(140)) + shrinkVertically(animationSpec = tween(140))
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftSage),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Divider),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = com.example.mobdata.ui.theme.Success,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Registro guardado exitosamente",
                        fontWeight = FontWeight.SemiBold,
                        color = Spruce,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    if (onVerHistorial != null) {
                        TextButton(onClick = onVerHistorial) {
                            Text("VER EN HISTORIAL", color = Teal, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    TextButton(onClick = onOcultar) {
                        Text("OK", color = Teal, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Botón principal de guardado
@Composable
fun BotonGuardar(modoEdicion: Boolean, onClick: () -> Unit) {
    BotonPrincipal(
        texto = if (modoEdicion) "Actualizar registro" else "Guardar en el teléfono",
        onClick = onClick,
        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
    )
}

// Estado de sincronización del registro (RNF01)
@Composable
fun EtiquetaSincronizacion(sincronizado: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(
            imageVector = if (sincronizado) Icons.Filled.CloudDone else Icons.Filled.Smartphone,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = TextSecondary
        )
        Text(
            if (sincronizado) "Sincronizado"
            else "Guardado solo en el teléfono (pendiente de sincronizar)",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
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

// Flecha decorativa usada en tarjetas destacadas
@Composable
fun FlechaAvance() {
    Icon(
        Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = Surface,
        modifier = Modifier.size(16.dp)
    )
}
