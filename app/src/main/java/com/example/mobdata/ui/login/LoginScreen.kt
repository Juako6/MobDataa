package com.example.mobdata.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobdata.data.Usuario
import com.example.mobdata.viewmodel.LoginViewModel

// ==========================================
// PANTALLA: acceso local con alias + contraseña (RF01).
// Dos modos en la misma pantalla: INICIAR SESIÓN y CREAR CUENTA.
// Toda la validación y el acceso a Room viven en LoginViewModel.
// ==========================================
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (Usuario) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "MobData",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Todos nos cuidamos entre todos",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // RNF04: declaración de alcance visible antes de ingresar
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                "Prototipo académico de autorregistro. No entrega diagnósticos ni " +
                    "tratamientos, no reemplaza a un profesional de la salud y no es un " +
                    "servicio de urgencias. Usa solo datos ficticios.",
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de modo: 0 = iniciar sesión, 1 = crear cuenta
        PrimaryTabRow(selectedTabIndex = pestana, modifier = Modifier.fillMaxWidth()) {
            Tab(
                selected = pestana == 0,
                onClick = { pestana = 0; viewModel.cambiarModo(esRegistro = false) },
                text = { Text("Iniciar sesión") }
            )
            Tab(
                selected = pestana == 1,
                onClick = { pestana = 1; viewModel.cambiarModo(esRegistro = true) },
                text = { Text("Crear cuenta") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = estado.alias,
            onValueChange = viewModel::actualizarAlias,
            label = { Text("Alias ficticio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estado.password,
            onValueChange = viewModel::actualizarPassword,
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = if (estado.esRegistro) ImeAction.Next else ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (estado.esRegistro) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = estado.confirmacionPassword,
                onValueChange = viewModel::actualizarConfirmacionPassword,
                label = { Text("Repite la contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mensaje de error único (validación o fallo de autenticación)
        estado.mensajeError?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.enviar(onLoginSuccess) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (estado.esRegistro) "CREAR CUENTA" else "ENTRAR")
            }
        }

        // La recuperación de contraseña quedó fuera de alcance (decisión del proyecto)
        if (!estado.esRegistro) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { pestana = 1; viewModel.cambiarModo(esRegistro = true) }) {
                Text("¿No tienes cuenta? Crear una", fontSize = 13.sp)
            }
        }
    }
}
