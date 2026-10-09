package com.example.mobdata.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.viewmodel.AnimoViewModel

// ==========================================
// PANTALLA: Detalle de un registro (RF03)
// Recibe el id del registro por argumento de navegación
// y lo busca en el ViewModel compartido
// ==========================================
@Composable
fun DetalleRegistroScreen(
    registroId: Long,
    viewModel: AnimoViewModel,
    onVolver: () -> Unit
) {
    val registro = viewModel.buscarPorId(registroId)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Detalle del registro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (registro == null) {
            Text(
                "No se encontró el registro solicitado.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        registro.emocion,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Intensidad", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(
                        "${registro.intensidad} / 5",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nota", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(registro.nota, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Fecha", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Text(registro.fechaHora, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("VOLVER AL HISTORIAL")
        }
    }
}
