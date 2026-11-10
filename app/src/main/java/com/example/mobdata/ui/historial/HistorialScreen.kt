package com.example.mobdata.ui.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.model.Ambito
import com.example.mobdata.model.ItemHistorial
import com.example.mobdata.model.formatearFecha
import com.example.mobdata.ui.componentes.EtiquetaSincronizacion
import com.example.mobdata.viewmodel.HistorialViewModel

// ==========================================
// PANTALLA: Historial cronológico de todos los registros guardados en Room,
// filtrable por ámbito. Cada tarjeta navega al detalle (editar / eliminar).
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    onVerDetalle: (Ambito, Long) -> Unit
) {
    val registros by viewModel.registros.collectAsState()
    val filtro by viewModel.filtro.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Historial de registros",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Filtro por ámbito
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = filtro == null,
                onClick = { viewModel.filtrarPor(null) },
                label = { Text("Todos") }
            )
            Ambito.entries.forEach { ambito ->
                FilterChip(
                    selected = filtro == ambito,
                    onClick = { viewModel.filtrarPor(ambito) },
                    label = { Text(ambito.etiqueta) },
                    leadingIcon = {
                        Icon(
                            imageVector = ambito.icono,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val lista = registros
        when {
            lista == null -> Unit // Room aún no emite la primera lectura
            lista.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay registros guardados aún.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // key + animateItem: el registro nuevo aparece con animación al guardar
                items(lista, key = { it.clave }) { item ->
                    TarjetaRegistro(
                        item = item,
                        modifier = Modifier.animateItem(),
                        onClick = { onVerDetalle(item.ambito, item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRegistro(
    item: ItemHistorial,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.ambito.icono,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    item.ambito.etiqueta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    item.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    item.resumen,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    formatearFecha(item.fecha),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                EtiquetaSincronizacion(item.sincronizado)
            }
        }
    }
}
