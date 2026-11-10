package com.example.mobdata.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = pestana == 0,
                    onClick = { onCambiarPestana(0) },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Filled.EditNote, contentDescription = null) },
                    colors = coloresItemNav()
                )
                NavigationBarItem(
                    selected = pestana == 1,
                    onClick = { onCambiarPestana(1) },
                    label = { Text("Historial") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
                    colors = coloresItemNav()
                )
                NavigationBarItem(
                    selected = pestana == 2,
                    onClick = { onCambiarPestana(2) },
                    label = { Text("Recursos") },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                    colors = coloresItemNav()
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                EncabezadoUsuario(alias = alias, onLogout = onLogout)

                Spacer(modifier = Modifier.height(16.dp))

                when (pestana) {
                    0 -> InicioContent(onNuevoRegistro)
                    1 -> HistorialScreen(historialViewModel, onVerDetalle)
                    else -> RecursosScreen(catalogoViewModel)
                }
            }
        }
    }
}

@Composable
private fun coloresItemNav() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)

// Saludo al usuario + acción de cerrar sesión
@Composable
private fun EncabezadoUsuario(alias: String, onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Hola,",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                alias.ifBlank { "usuario" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = onLogout) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.primary
            )
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
        Text(
            "¿Qué quieres registrar?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Ambito.entries.forEach { ambito ->
            TarjetaAmbito(ambito = ambito, onClick = { onNuevoRegistro(ambito) })
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Prototipo académico de autorregistro: no entrega diagnósticos ni " +
                        "tratamientos y no es un servicio de urgencias.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

// Tarjeta de módulo con icono en círculo de color de la identidad
@Composable
private fun TarjetaAmbito(ambito: Ambito, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ambito.icono,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    ambito.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    ambito.descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
