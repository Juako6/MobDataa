package com.example.mobdata.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.mobdata.viewmodel.LoginViewModel

// ==========================================
// PANTALLA: Login con alias ficticio (RF01)
// La lógica de validación vive en LoginViewModel
// ==========================================
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Lemac DataLab",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Autorregistro en Salud Mental",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = estado.alias,
            onValueChange = viewModel::actualizarAlias,
            label = { Text("Alias Ficticio") },
            isError = estado.mensajeError != null,
            supportingText = {
                estado.mensajeError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.validarYIngresar(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("INGRESAR")
        }
    }
}
