package com.example.mobdata.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.ui.animo.FormularioAnimoScreen
import com.example.mobdata.ui.historial.HistorialScreen
import com.example.mobdata.viewmodel.AnimoViewModel

// ==========================================
// PANTALLA: Dashboard con pestañas Registro / Historial
// ==========================================
@Composable
fun DashboardScreen(
    alias: String,
    viewModel: AnimoViewModel,
    onLogout: () -> Unit,
    onVerDetalle: (Long) -> Unit = {}
) {
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    label = { Text("Registro") },
                    icon = { Text("📝") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    label = { Text("Historial") },
                    icon = { Text("📊") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Buenas $alias", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onLogout) { Text("Salir") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (pestanaSeleccionada) {
                    0 -> FormularioAnimoScreen(viewModel)
                    1 -> HistorialScreen(viewModel, onVerDetalle)
                }
            }
        }
    }
}
