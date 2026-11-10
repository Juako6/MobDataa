package com.example.mobdata.ui.recursos

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.data.ContenidoCatalogo
import com.example.mobdata.data.TiposContenido
import com.example.mobdata.viewmodel.CatalogoViewModel

// ==========================================
// PANTALLA (RF10): biblioteca de recursos.
// Guías de habilidades DBT y lecturas psicoeducativas leídas desde Room.
// ==========================================
@Composable
fun RecursosScreen(viewModel: CatalogoViewModel) {
    val contenidos by viewModel.contenidos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Biblioteca de recursos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Contenido educativo. No reemplaza la atención de un profesional.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        val lecturas = contenidos.filter { it.tipo == TiposContenido.PSICOEDUCATIVO }
        val habilidades = contenidos.filter { it.tipo == TiposContenido.HABILIDAD_DBT }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (lecturas.isNotEmpty()) {
                item(key = "titulo-lecturas") { SeccionTitulo("Lecturas psicoeducativas") }
                items(lecturas, key = { "c-${it.id}" }) { TarjetaContenido(it) }
            }
            if (habilidades.isNotEmpty()) {
                item(key = "titulo-dbt") { SeccionTitulo("Guías de habilidades DBT") }
                items(habilidades, key = { "c-${it.id}" }) { TarjetaContenido(it, mostrarCategoria = true) }
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        texto,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TarjetaContenido(contenido: ContenidoCatalogo, mostrarCategoria: Boolean = false) {
    var abierto by remember { mutableStateOf(false) }

    Card(
        onClick = { abierto = !abierto },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        contenido.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (mostrarCategoria) {
                        Text(
                            contenido.categoria,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (abierto) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(visible = abierto) {
                Text(
                    contenido.descripcion,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
