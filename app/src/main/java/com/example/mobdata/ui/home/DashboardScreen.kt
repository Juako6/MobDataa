package com.example.mobdata.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.model.Ambito
import com.example.mobdata.ui.historial.HistorialScreen
import com.example.mobdata.ui.recursos.RecursosScreen
import com.example.mobdata.viewmodel.CatalogoViewModel
import com.example.mobdata.viewmodel.HistorialViewModel

// ==========================================
// PANTALLA: Dashboard con pestañas Inicio / Historial / Recursos.
// La pestaña activa vive en la navegación (se conserva al volver de un formulario).
// ==========================================
@Composable
fun DashboardScreen(
    alias: String,
    pestana: Int,
    onCambiarPestana: (Int) -> Unit,
    historialViewModel: HistorialViewModel,
    catalogoViewModel: CatalogoViewModel,
    onNuevoRegistro: (Ambito) -> Unit,
    onVerDetalle: (Ambito, Long) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestana == 0,
                    onClick = { onCambiarPestana(0) },
                    label = { Text("Inicio") },
                    icon = { Text("📝") }
                )
                NavigationBarItem(
                    selected = pestana == 1,
                    onClick = { onCambiarPestana(1) },
                    label = { Text("Historial") },
                    icon = { Text("📊") }
                )
                NavigationBarItem(
                    selected = pestana == 2,
                    onClick = { onCambiarPestana(2) },
                    label = { Text("Recursos") },
                    icon = { Text("📚") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hola, $alias", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onLogout) { Text("Salir") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (pestana) {
                    0 -> InicioContent(onNuevoRegistro)
                    1 -> HistorialScreen(historialViewModel, onVerDetalle)
                    else -> RecursosScreen(catalogoViewModel)
                }
            }
        }
    }
}

// Elección del módulo en el que se quiere registrar un evento
@Composable
private fun InicioContent(onNuevoRegistro: (Ambito) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("¿Qué quieres registrar?", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Ambito.entries.forEach { ambito ->
            Card(
                onClick = { onNuevoRegistro(ambito) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ambito.icono, fontSize = 30.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(ambito.etiqueta, fontWeight = FontWeight.Bold)
                        Text(ambito.descripcion, fontSize = 13.sp)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                "Prototipo académico de autorregistro: no entrega diagnósticos ni " +
                    "tratamientos y no es un servicio de urgencias.",
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
