package com.example.mobdata.model

import com.example.mobdata.data.RegistroAnimo
import com.example.mobdata.data.RegistroDbt
import com.example.mobdata.data.RegistroNeurodesarrollo

// ==========================================
// Elemento unificado del historial: permite listar en una sola vista
// registros de los distintos ámbitos y mostrar su detalle genérico.
// ==========================================
data class ItemHistorial(
    val ambito: Ambito,
    val id: Long,
    val titulo: String,
    val resumen: String,
    val fecha: Long,
    val sincronizado: Boolean,
    val campos: List<Pair<String, String>>
) {
    // Los id se repiten entre tablas, por eso la clave combina ámbito + id
    val clave: String get() = "${ambito.name}-$id"
}

private fun String.oGuion(): String = if (isBlank()) "—" else this

fun RegistroAnimo.aItemHistorial() = ItemHistorial(
    ambito = Ambito.ANIMO,
    id = id,
    titulo = emocion,
    resumen = "Intensidad $intensidad/5 · $nota",
    fecha = fecha,
    sincronizado = sincronizado,
    campos = listOf(
        "Emoción" to emocion,
        "Intensidad" to "$intensidad / 5",
        "Nota" to nota.oGuion()
    )
)

fun RegistroNeurodesarrollo.aItemHistorial() = ItemHistorial(
    ambito = Ambito.NEURODESARROLLO,
    id = id,
    titulo = rutina,
    resumen = "Episodio: $tipoEpisodio · Resultado $resultado/5",
    fecha = fecha,
    sincronizado = sincronizado,
    campos = listOf(
        "Rutina" to rutina,
        "Organización de tareas" to organizacionTareas.oGuion(),
        "Episodio" to tipoEpisodio,
        "Estrategia utilizada" to estrategia,
        "Resultado percibido" to "$resultado / 5"
    )
)

fun RegistroDbt.aItemHistorial() = ItemHistorial(
    ambito = Ambito.DBT,
    id = id,
    titulo = habilidad,
    resumen = "Intensidad $intensidad/5 · Utilidad $utilidad/5",
    fecha = fecha,
    sincronizado = sincronizado,
    campos = listOf(
        "Habilidad practicada" to habilidad,
        "Categoría" to categoria.oGuion(),
        "Emoción o situación" to situacion,
        "Intensidad" to "$intensidad / 5",
        "Utilidad percibida" to "$utilidad / 5"
    )
)
