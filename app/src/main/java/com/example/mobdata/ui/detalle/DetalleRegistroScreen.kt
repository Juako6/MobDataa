package com.example.mobdata.ui.detalle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.mobdata.model.Ambito
import com.example.mobdata.model.formatearFecha
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.componentes.EtiquetaSincronizacion
import com.example.mobdata.viewmodel.HistorialViewModel

// ==========================================
// PANTALLA (RF04): detalle de un registro con EDITAR y ELIMINAR.
// Recibe ámbito + id por argumentos de navegación y busca el registro
// en el estado reactivo (Room).
// ==========================================
@Composable
fun DetalleRegistroScreen(
    ambito: Ambito,
    registroId: Long,
    viewModel: HistorialViewModel,
    onVolver: () -> Unit,
    onEditar: () -> Unit
) {
    val todos by viewModel.todos.collectAsState()
    val item = todos?.find { it.ambito == ambito && it.id == registroId }
    var confirmarEliminar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoPantalla(titulo = "Detalle del registro", onVolver = onVolver)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            todos == null -> Unit
            item == null -> Text(
                "No se encontró el registro solicitado.",
                color = MaterialTheme.colorScheme.error
            )
            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "${ambito.icono} ${ambito.etiqueta}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        item.campos.forEach { (etiqueta, valor) ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                etiqueta,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(valor, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Fecha",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(formatearFecha(item.fecha), fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        EtiquetaSincronizacion(item.sincronizado)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
                    Text("EDITAR")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { confirmarEliminar = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("ELIMINAR")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("VOLVER AL HISTORIAL")
        }
    }

    // Confirmación antes de borrar
    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar registro") },
            text = { Text("Este registro se borrará del teléfono y no se puede recuperar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminar(ambito, registroId)
                    onVolver()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
