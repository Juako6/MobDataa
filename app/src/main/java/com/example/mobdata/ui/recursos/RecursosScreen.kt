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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
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
            style = MaterialTheme.typography.headlineMedium,
            color = Spruce
        )
        Text(
            "Contenido educativo. No reemplaza la atención de un profesional.",
            fontSize = 13.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        val lecturas = contenidos.filter { it.tipo == TiposContenido.PSICOEDUCATIVO }
        val habilidades = contenidos.filter { it.tipo == TiposContenido.HABILIDAD_DBT }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
        fontWeight = FontWeight.SemiBold,
        color = Spruce,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun TarjetaContenido(contenido: ContenidoCatalogo, mostrarCategoria: Boolean = false) {
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
                        Icons.Filled.MenuBook,
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
                Icon(
                    imageVector = if (abierto) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (abierto) "Contraer" else "Expandir",
                    tint = TextSecondary
                )
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
