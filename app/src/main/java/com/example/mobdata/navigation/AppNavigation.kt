package com.example.mobdata.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobdata.model.Ambito
import com.example.mobdata.ui.animo.FormularioAnimoScreen
import com.example.mobdata.ui.dbt.FormularioDbtScreen
import com.example.mobdata.ui.detalle.DetalleRegistroScreen
import com.example.mobdata.ui.home.DashboardScreen
import com.example.mobdata.ui.login.LoginScreen
import com.example.mobdata.ui.neurodesarrollo.FormularioNeurodesarrolloScreen
import com.example.mobdata.viewmodel.AnimoViewModel
import com.example.mobdata.viewmodel.CatalogoViewModel
import com.example.mobdata.viewmodel.DbtViewModel
import com.example.mobdata.viewmodel.HistorialViewModel
import com.example.mobdata.viewmodel.NeurodesarrolloViewModel

// ==========================================
// NAVEGACIÓN con NavController + NavHost
// LOGIN -> HOME (Inicio / Historial / Recursos)
//            ├─ FORMULARIO/{ambito}/{registroId}  (0 = nuevo, >0 = editar)
//            └─ DETALLE/{ambito}/{registroId}     (editar / eliminar)
// ==========================================
object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val FORMULARIO = "formulario/{ambito}/{registroId}"
    const val DETALLE = "detalle/{ambito}/{registroId}"

    // Clave en el SavedStateHandle de HOME para recordar la pestaña activa
    const val CLAVE_PESTANA = "pestana"

    fun formulario(ambito: Ambito, registroId: Long = 0L) = "formulario/${ambito.name}/$registroId"
    fun detalle(ambito: Ambito, registroId: Long) = "detalle/${ambito.name}/$registroId"
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

    // ViewModels compartidos entre pantallas (ámbito de la Activity)
    val animoViewModel: AnimoViewModel = viewModel()
    val neuroViewModel: NeurodesarrolloViewModel = viewModel()
    val dbtViewModel: DbtViewModel = viewModel()
    val historialViewModel: HistorialViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()

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

        composable(Rutas.HOME) { entry ->
            // La pestaña activa se guarda en la entrada de navegación
            val pestana by entry.savedStateHandle
                .getStateFlow(Rutas.CLAVE_PESTANA, 0)
                .collectAsState()

            DashboardScreen(
                alias = sharedPref.getString("alias", "") ?: "",
                pestana = pestana,
                onCambiarPestana = { entry.savedStateHandle[Rutas.CLAVE_PESTANA] = it },
                historialViewModel = historialViewModel,
                catalogoViewModel = catalogoViewModel,
                onNuevoRegistro = { ambito ->
                    navController.navigate(Rutas.formulario(ambito))
                },
                // PASO DE INFORMACIÓN: ámbito e id del registro viajan en la ruta
                onVerDetalle = { ambito, registroId ->
                    navController.navigate(Rutas.detalle(ambito, registroId))
                },
                onLogout = {
                    sharedPref.edit().clear().apply()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.FORMULARIO,
            arguments = listOf(
                navArgument("ambito") { type = NavType.StringType },
                navArgument("registroId") { type = NavType.LongType }
            )
        ) { entry ->
            val ambito = Ambito.desdeNombre(entry.arguments?.getString("ambito")) ?: Ambito.ANIMO
            val registroId = entry.arguments?.getLong("registroId") ?: 0L
            val volver: () -> Unit = { navController.popBackStack() }
            // "Ver en historial": vuelve a HOME dejando seleccionada la pestaña Historial
            val verHistorial: () -> Unit = {
                navController.getBackStackEntry(Rutas.HOME)
                    .savedStateHandle[Rutas.CLAVE_PESTANA] = 1
                navController.popBackStack(Rutas.HOME, inclusive = false)
            }

            when (ambito) {
                Ambito.ANIMO -> FormularioAnimoScreen(animoViewModel, registroId, volver, verHistorial)
                Ambito.NEURODESARROLLO ->
                    FormularioNeurodesarrolloScreen(neuroViewModel, registroId, volver, verHistorial)
                Ambito.DBT -> FormularioDbtScreen(dbtViewModel, registroId, volver, verHistorial)
            }
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(
                navArgument("ambito") { type = NavType.StringType },
                navArgument("registroId") { type = NavType.LongType }
            )
        ) { entry ->
            val ambito = Ambito.desdeNombre(entry.arguments?.getString("ambito")) ?: Ambito.ANIMO
            val registroId = entry.arguments?.getLong("registroId") ?: 0L
            DetalleRegistroScreen(
                ambito = ambito,
                registroId = registroId,
                viewModel = historialViewModel,
                onVolver = { navController.popBackStack() },
                onEditar = { navController.navigate(Rutas.formulario(ambito, registroId)) }
            )
        }
    }
}
