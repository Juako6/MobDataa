package com.example.mobdata.ui.recursos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.data.ContenidoCatalogo
import com.example.mobdata.data.TiposContenido
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
import com.example.mobdata.viewmodel.CatalogoViewModel

// ==========================================
// PANTALLA (RF10): biblioteca de recursos y administración del catálogo.
// Guías de habilidades DBT y lecturas psicoeducativas leídas desde Room.
// ==========================================
@Composable
fun RecursosScreen(viewModel: CatalogoViewModel) {
    val contenidos by viewModel.contenidos.collectAsState()
    var modoAdmin by remember { mutableStateOf(false) }
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var contenidoAEditar by remember { mutableStateOf<ContenidoCatalogo?>(null) }
    var contenidoAEliminar by remember { mutableStateOf<ContenidoCatalogo?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Biblioteca de recursos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Spruce
                )
                Text(
                    "Contenido educativo. No reemplaza la atención de un profesional.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            TextButton(onClick = { modoAdmin = !modoAdmin }) {
                Text(
                    if (modoAdmin) "Listo" else "Administrar",
                    color = Teal,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (modoAdmin) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { mostrarDialogoCrear = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Teal)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Teal)
                Text(
                    "Nuevo contenido",
                    color = Teal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val lecturas = contenidos.filter { it.tipo == TiposContenido.PSICOEDUCATIVO }
        val habilidades = contenidos.filter { it.tipo == TiposContenido.HABILIDAD_DBT }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (lecturas.isNotEmpty()) {
                item(key = "titulo-lecturas") { SeccionTitulo("Lecturas psicoeducativas") }
                items(lecturas, key = { "c-${it.id}" }) {
                    TarjetaContenido(
                        contenido = it,
                        mostrarCategoria = false,
                        modoAdmin = modoAdmin,
                        onEditar = { contenidoAEditar = it },
                        onEliminar = { contenidoAEliminar = it }
                    )
                }
            }
            if (habilidades.isNotEmpty()) {
                item(key = "titulo-dbt") { SeccionTitulo("Guías de habilidades DBT") }
                items(habilidades, key = { "c-${it.id}" }) {
                    TarjetaContenido(
                        contenido = it,
                        mostrarCategoria = true,
                        modoAdmin = modoAdmin,
                        onEditar = { contenidoAEditar = it },
                        onEliminar = { contenidoAEliminar = it }
                    )
                }
            }
        }
    }

    if (mostrarDialogoCrear) {
        DialogoContenidoCatalogo(
            tituloDialogo = "Nuevo contenido",
            contenidoInicial = null,
            onDismiss = { mostrarDialogoCrear = false },
            onGuardar = { tipo, categoria, titulo, descripcion ->
                viewModel.agregarContenido(tipo, categoria, titulo, descripcion)
                mostrarDialogoCrear = false
            }
        )
    }

    contenidoAEditar?.let { item ->
        DialogoContenidoCatalogo(
            tituloDialogo = "Editar contenido",
            contenidoInicial = item,
            onDismiss = { contenidoAEditar = null },
            onGuardar = { tipo, categoria, titulo, descripcion ->
                viewModel.actualizarContenido(
                    item.copy(
                        tipo = tipo,
                        categoria = categoria,
                        titulo = titulo,
                        descripcion = descripcion
                    )
                )
                contenidoAEditar = null
            }
        )
    }

    contenidoAEliminar?.let { item ->
        AlertDialog(
            onDismissRequest = { contenidoAEliminar = null },
            title = { Text("Eliminar contenido", color = Spruce) },
            text = { Text("¿Seguro que quieres eliminar \"${item.titulo}\" del catálogo?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarContenido(item)
                        contenidoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { contenidoAEliminar = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        texto,
        fontWeight = FontWeight.SemiBold,
        color = Spruce,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TarjetaContenido(
    contenido: ContenidoCatalogo,
    mostrarCategoria: Boolean = false,
    modoAdmin: Boolean = false,
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var abierto by remember { mutableStateOf(false) }

    Card(
        onClick = { abierto = !abierto },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SoftSage, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Teal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        contenido.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = Spruce
                    )
                    if (mostrarCategoria) {
                        Text(
                            contenido.categoria,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                if (modoAdmin) {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Teal)
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (abierto) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (abierto) "Contraer" else "Expandir",
                        tint = TextSecondary
                    )
                }
            }
            AnimatedVisibility(visible = abierto) {
                Text(
                    contenido.descripcion,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun DialogoContenidoCatalogo(
    tituloDialogo: String,
    contenidoInicial: ContenidoCatalogo?,
    onDismiss: () -> Unit,
    onGuardar: (tipo: String, categoria: String, titulo: String, descripcion: String) -> Unit
) {
    var tipo by remember { mutableStateOf(contenidoInicial?.tipo ?: TiposContenido.HABILIDAD_DBT) }
    var categoria by remember { mutableStateOf(contenidoInicial?.categoria ?: "") }
    var titulo by remember { mutableStateOf(contenidoInicial?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(contenidoInicial?.descripcion ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tituloDialogo, color = Spruce) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tipo == TiposContenido.HABILIDAD_DBT,
                        onClick = { tipo = TiposContenido.HABILIDAD_DBT },
                        label = { Text("Habilidad DBT") }
                    )
                    FilterChip(
                        selected = tipo == TiposContenido.PSICOEDUCATIVO,
                        onClick = { tipo = TiposContenido.PSICOEDUCATIVO },
                        label = { Text("Lectura") }
                    )
                }
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it; error = null },
                    label = { Text("Título *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it; error = null },
                    label = { Text("Descripción *") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isBlank() || descripcion.isBlank()) {
                        error = "Completa el título y la descripción."
                    } else {
                        onGuardar(tipo, categoria, titulo, descripcion)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}
