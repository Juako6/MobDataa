package com.example.mobdata.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobdata.ui.detalle.DetalleRegistroScreen
import com.example.mobdata.ui.home.DashboardScreen
import com.example.mobdata.ui.login.LoginScreen
import com.example.mobdata.viewmodel.AnimoViewModel

// ==========================================
// NAVEGACIÓN: destinos de la app con NavController + NavHost
// LOGIN -> HOME (pestañas Registro / Historial) -> DETALLE
// ==========================================
object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val DETALLE = "detalle/{registroId}"

    fun detalle(registroId: Long) = "detalle/$registroId"
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val sharedPref = remember {
        context.getSharedPreferences("LemacPrefs", Context.MODE_PRIVATE)
    }
    val aliasGuardado = remember {
        sharedPref.getString("alias", "") ?: ""
    }

    val navController = rememberNavController()

    // ViewModel único compartido entre pantallas (ámbito de la Activity)
    val animoViewModel: AnimoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = if (aliasGuardado.isEmpty()) Rutas.LOGIN else Rutas.HOME
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginSuccess = { alias ->
                    sharedPref.edit().putString("alias", alias).apply()
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.HOME) {
            DashboardScreen(
                alias = sharedPref.getString("alias", "") ?: "",
                viewModel = animoViewModel,
                onLogout = {
                    sharedPref.edit().clear().apply()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                // PASO DE INFORMACIÓN: el id del registro viaja en la ruta
                onVerDetalle = { registroId ->
                    navController.navigate(Rutas.detalle(registroId))
                }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(
                navArgument("registroId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val registroId = backStackEntry.arguments?.getLong("registroId") ?: 0L
            DetalleRegistroScreen(
                registroId = registroId,
                viewModel = animoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
