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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdata.model.Ambito
import com.example.mobdata.ui.historial.HistorialScreen
import com.example.mobdata.ui.recursos.RecursosScreen
import com.example.mobdata.ui.theme.Accent
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.Paper
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
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
        containerColor = Paper,
        topBar = {
            // Encabezado: identifica la app y ofrece salir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Paper)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).background(Teal, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MD", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("MobData", color = Spruce, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Autorregistro", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                IconButton(onClick = onLogout) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Teal
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = pestana == 0,
                    onClick = { onCambiarPestana(0) },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Filled.EditNote, contentDescription = "Ir a inicio") },
                    colors = coloresItemNav()
                )
                NavigationBarItem(
                    selected = pestana == 1,
                    onClick = { onCambiarPestana(1) },
                    label = { Text("Historial") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Ver historial") },
                    colors = coloresItemNav()
                )
                NavigationBarItem(
                    selected = pestana == 2,
                    onClick = { onCambiarPestana(2) },
                    label = { Text("Recursos") },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Abrir recursos") },
                    colors = coloresItemNav()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(top = 6.dp, bottom = 20.dp)
        ) {
            when (pestana) {
                0 -> InicioContent(alias = alias, onNuevoRegistro = onNuevoRegistro)
                1 -> HistorialScreen(historialViewModel, onVerDetalle)
                else -> RecursosScreen(catalogoViewModel)
            }
        }
    }
}

@Composable
private fun coloresItemNav() = NavigationBarItemDefaults.colors(
    selectedIconColor = Spruce,
    selectedTextColor = Spruce,
    indicatorColor = SoftSage,
    unselectedIconColor = TextSecondary,
    unselectedTextColor = TextSecondary
)

// Elección del módulo en el que se quiere registrar un evento
@Composable
private fun InicioContent(alias: String, onNuevoRegistro: (Ambito) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SaludoUsuario(alias = alias)

        // Tarjeta destacada en verde profundo
        Card(
            onClick = { onNuevoRegistro(Ambito.ANIMO) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Spruce),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Registrar cómo te sientes",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Toma menos de un minuto y queda solo en tu teléfono",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Empezar ahora",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Icon(
                    Icons.Filled.Assignment,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            "¿Qué quieres registrar?",
            style = MaterialTheme.typography.headlineMedium,
            color = Spruce,
            modifier = Modifier.padding(top = 4.dp)
        )

        Ambito.entries.forEach { ambito ->
            TarjetaAmbito(ambito = ambito, onClick = { onNuevoRegistro(ambito) })
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = com.example.mobdata.ui.theme.AttentionBg),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                "Prototipo académico de autorregistro: no entrega diagnósticos ni " +
                    "tratamientos y no es un servicio de urgencias.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = com.example.mobdata.ui.theme.AttentionInk,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

// Saludo con círculo de iniciales
@Composable
private fun SaludoUsuario(alias: String) {
    val nombre = alias.ifBlank { "usuario" }
    val primerNombre = nombre.substringBefore(" ").ifBlank { nombre }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Hola, $primerNombre",
                color = Spruce,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Cuida tu bienestar un registro a la vez",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Box(
            modifier = Modifier.size(48.dp).background(SoftSage, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                primerNombre.take(1).uppercase(),
                color = Spruce,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Tarjeta de módulo con icono en círculo de color de la identidad
@Composable
private fun TarjetaAmbito(ambito: Ambito, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(SoftSage, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ambito.icono,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    ambito.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    color = Spruce
                )
                Text(
                    ambito.descripcion,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
