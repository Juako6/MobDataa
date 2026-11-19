package com.example.mobdata.ui.detalle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.model.Ambito
import com.example.mobdata.model.formatearFecha
import com.example.mobdata.ui.componentes.BotonPrincipal
import com.example.mobdata.ui.componentes.BotonSecundario
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.componentes.EtiquetaSincronizacion
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.Error
import com.example.mobdata.ui.theme.Paper
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
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
            .background(Paper)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoPantalla(titulo = "Detalle del registro", onVolver = onVolver)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            todos == null -> Unit
            item == null -> Text(
                "No se encontró el registro solicitado.",
                color = Error
            )
            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Divider),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(SoftSage, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ambito.icono,
                                    contentDescription = null,
                                    tint = Teal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                ambito.etiqueta,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Spruce,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }

                        item.campos.forEach { (etiqueta, valor) ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                etiqueta,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Text(valor, fontSize = 16.sp, color = Spruce)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "Fecha",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(formatearFecha(item.fecha), fontSize = 16.sp, color = Spruce)

                        Spacer(modifier = Modifier.height(14.dp))
                        EtiquetaSincronizacion(item.sincronizado)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BotonPrincipal(texto = "Editar registro", onClick = onEditar)
                Spacer(modifier = Modifier.height(10.dp))
                BotonSecundario(
                    texto = "Eliminar registro",
                    onClick = { confirmarEliminar = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("VOLVER AL HISTORIAL", color = Teal, fontWeight = FontWeight.SemiBold)
        }
    }

    // Confirmación antes de borrar
    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            shape = RoundedCornerShape(16.dp),
            title = { Text("Eliminar registro", color = Spruce) },
            text = { Text("Este registro se borrará del teléfono y no se puede recuperar.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminar(ambito, registroId)
                    onVolver()
                }) {
                    Text("Eliminar", color = Error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar", color = Teal, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
