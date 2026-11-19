package com.example.mobdata.ui.usuarios

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobdata.data.remoto.UsuarioApi
import com.example.mobdata.model.EstadoListaUsuarios
import com.example.mobdata.ui.componentes.BotonPrincipal
import com.example.mobdata.ui.componentes.EncabezadoPantalla
import com.example.mobdata.ui.theme.Divider
import com.example.mobdata.ui.theme.Error
import com.example.mobdata.ui.theme.ErrorBg
import com.example.mobdata.ui.theme.Paper
import com.example.mobdata.ui.theme.SoftSage
import com.example.mobdata.ui.theme.Spruce
import com.example.mobdata.ui.theme.Surface
import com.example.mobdata.ui.theme.Teal
import com.example.mobdata.ui.theme.TextSecondary
import com.example.mobdata.viewmodel.UsuariosApiViewModel

@Composable
fun UsuariosApiScreen(
    viewModel: UsuariosApiViewModel = viewModel(),
    onVolver: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .padding(20.dp)
    ) {
        EncabezadoPantalla(titulo = "Equipo profesional", onVolver = onVolver)
        Text(
            text = "Listado obtenido mediante Retrofit y StateFlow.",
            fontSize = 13.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val actual = estado) {
            is EstadoListaUsuarios.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Teal)
                }
            }

            is EstadoListaUsuarios.Exito -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(actual.usuarios, key = { it.id }) { usuario ->
                        TarjetaUsuario(usuario)
                    }
                }
            }

            is EstadoListaUsuarios.Error -> {
                VistaError(
                    mensaje = actual.mensaje,
                    onReintentar = { viewModel.cargarUsuarios() }
                )
            }
        }
    }
}

@Composable
private fun VistaError(mensaje: String, onReintentar: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                mensaje,
                color = Error,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier
                    .background(ErrorBg, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )
            BotonPrincipal(
                texto = "Reintentar",
                onClick = onReintentar,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun TarjetaUsuario(usuario: UsuarioApi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(44.dp)
                    .background(SoftSage, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                    color = Teal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    usuario.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Spruce
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "@${usuario.alias} · ${usuario.email}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "  ${usuario.rol}  ·  Registrado: ${usuario.fechaRegistro}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (usuario.activo) "Activo" else "Inactivo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (usuario.activo) Teal else Error
                )
            }
        }
    }
}
