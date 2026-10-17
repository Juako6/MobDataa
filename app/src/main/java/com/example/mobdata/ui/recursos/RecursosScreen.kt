package com.example.mobdata.ui.recursos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        Text("Biblioteca de recursos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            "Contenido educativo. No reemplaza la atención de un profesional.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(contenido.titulo, fontWeight = FontWeight.Bold)
            if (mostrarCategoria) {
                Text(
                    contenido.categoria,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            AnimatedVisibility(visible = abierto) {
                Text(contenido.descripcion, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
            if (!abierto) {
                Text(
                    "Toca para leer",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
