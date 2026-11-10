package com.example.mobdata.ui.historial

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
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
            style = MaterialTheme.typography.headlineMedium,
            color = Spruce
        )
        Text(
            "Todos tus registros guardados en este teléfono.",
            fontSize = 13.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Filtro por ámbito
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FiltroChipHistorial(
                texto = "Todos",
                seleccionado = filtro == null,
                onClick = { viewModel.filtrarPor(null) }
            )
            Ambito.entries.forEach { ambito ->
                FiltroChipHistorial(
                    texto = ambito.etiqueta,
                    seleccionado = filtro == ambito,
                    icono = ambito.icono,
                    onClick = { viewModel.filtrarPor(ambito) }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        val lista = registros
        when {
            lista == null -> Unit // Room aún no emite la primera lectura
            lista.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay registros guardados aún.",
                    color = TextSecondary
                )
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
private fun FiltroChipHistorial(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    icono: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        leadingIcon = icono?.let {
            {
                Icon(it, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SoftSage,
            selectedLabelColor = Spruce,
            containerColor = Surface,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = seleccionado,
            borderColor = Divider,
            selectedBorderColor = Teal,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        )
    )
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SoftSage, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.ambito.icono,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    item.ambito.etiqueta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Teal
                )
                Text(
                    item.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = Spruce
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    item.resumen,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    formatearFecha(item.fecha),
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                EtiquetaSincronizacion(item.sincronizado)
            }
        }
    }
}
