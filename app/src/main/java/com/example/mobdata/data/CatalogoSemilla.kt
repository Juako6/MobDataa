package com.example.mobdata.data

// ==========================================
// CONTENIDO INICIAL del catálogo (RF10).
// Los ids son fijos: cargar la semilla dos veces es inofensivo (REPLACE).
// Textos de carácter educativo; no constituyen diagnóstico ni tratamiento.
// ==========================================
object CatalogoSemilla {

    private const val MINDFULNESS = "Mindfulness"
    private const val MALESTAR = "Tolerancia al malestar"
    private const val REGULACION = "Regulación emocional"
    private const val INTERPERSONAL = "Efectividad interpersonal"
    private const val LECTURA = "Lecturas"

    private fun habilidad(id: Long, categoria: String, titulo: String, descripcion: String) =
        ContenidoCatalogo(id, TiposContenido.HABILIDAD_DBT, categoria, titulo, descripcion)

    private fun lectura(id: Long, titulo: String, descripcion: String) =
        ContenidoCatalogo(id, TiposContenido.PSICOEDUCATIVO, LECTURA, titulo, descripcion)

    val contenidos: List<ContenidoCatalogo> = listOf(
        // ---- Mindfulness ----
        habilidad(1, MINDFULNESS, "Observar",
            "Prestar atención a lo que ocurre ahora (sonidos, sensaciones, pensamientos) sin intentar cambiarlo ni juzgarlo."),
        habilidad(2, MINDFULNESS, "Describir",
            "Poner en palabras lo que observas ciñéndote a los hechos, por ejemplo «noto tensión en los hombros» en lugar de interpretaciones."),
        habilidad(3, MINDFULNESS, "Participar",
            "Involucrarte por completo en la actividad del momento, sin quedarte pensando en otra cosa."),
        habilidad(4, MINDFULNESS, "Mente sabia",
            "Buscar el equilibrio entre la mente emocional y la mente racional antes de decidir cómo actuar."),

        // ---- Tolerancia al malestar ----
        habilidad(5, MALESTAR, "Respiración pausada",
            "Respirar lento y alargar la exhalación para ayudar al cuerpo a bajar la activación."),
        habilidad(6, MALESTAR, "Distracción",
            "Desviar la atención de forma temporal hacia actividades absorbentes (caminar, ordenar, escuchar música) mientras la intensidad baja."),
        habilidad(7, MALESTAR, "Autocalmarse con los cinco sentidos",
            "Usar vista, oído, olfato, gusto y tacto para generar confort: música tranquila, una infusión, una manta agradable."),
        habilidad(8, MALESTAR, "Pros y contras",
            "Comparar por escrito las consecuencias de actuar según un impulso frente a tolerarlo."),
        habilidad(9, MALESTAR, "Aceptación radical",
            "Reconocer la realidad tal como es en este momento, sin aprobarla, para dejar de gastar energía en resistirla."),

        // ---- Regulación emocional ----
        habilidad(10, REGULACION, "Identificar y nombrar emociones",
            "Ponerle nombre a lo que sientes y notar qué lo desencadenó, cómo se siente en el cuerpo y qué impulso trae."),
        habilidad(11, REGULACION, "Verificar los hechos",
            "Contrastar tu interpretación de la situación con los hechos observables para ver si la emoción se ajusta a ellos."),
        habilidad(12, REGULACION, "Acción opuesta",
            "Cuando la emoción no se ajusta a los hechos o no ayuda, actuar de forma contraria al impulso que trae."),
        habilidad(13, REGULACION, "Resolución de problemas",
            "Definir el problema, pensar alternativas, elegir una y probarla, y luego revisar cómo resultó."),
        habilidad(14, REGULACION, "Cuidar el cuerpo (PLEASE)",
            "Cuidar sueño, alimentación y actividad física para reducir la vulnerabilidad emocional cotidiana."),

        // ---- Efectividad interpersonal ----
        habilidad(15, INTERPERSONAL, "DEAR MAN",
            "Pedir algo o decir que no con claridad: describir, expresar, afirmar, reforzar, mantenerse atento, aparentar confianza y negociar."),
        habilidad(16, INTERPERSONAL, "GIVE",
            "Cuidar la relación: ser gentil, mostrar interés, validar al otro y mantener un tono relajado."),
        habilidad(17, INTERPERSONAL, "FAST",
            "Cuidar el respeto por uno mismo: ser justo, no pedir disculpas en exceso, mantener tus valores y ser veraz."),

        // ---- Lecturas psicoeducativas ----
        lectura(101, "¿Qué es el autorregistro?",
            "Anotar brevemente cómo te sientes y qué hiciste ayuda a descubrir patrones con el tiempo. Mientras más cerca del momento registres, más fiel es el recuerdo. No necesitas escribir mucho: las escalas de 1 a 5 existen para que sea rápido."),
        lectura(102, "Sobrecarga y autorregulación",
            "La sobrecarga aparece cuando las demandas del momento superan los recursos disponibles. La autorregulación son las acciones que usas para recuperar el equilibrio (pausar, bajar estímulos, respirar, pedir apoyo). Registrar qué hiciste y cómo resultó permite ver qué estrategias te funcionan."),
        lectura(103, "Cómo usar las escalas de intensidad",
            "Las escalas de 1 a 5 son personales: lo importante es compararte contigo mismo/a a lo largo del tiempo, no con otras personas. Si dudas entre dos valores, elige el primero que se te ocurra."),
        lectura(104, "Alcance de esta aplicación",
            "MobData es un prototipo académico de autorregistro. No entrega diagnósticos ni tratamientos, no reemplaza a un profesional de la salud y no es un servicio de urgencias. Si necesitas ayuda inmediata, comunícate con un profesional o con los servicios de emergencia de tu localidad. Usa solo datos ficticios: no ingreses nombres reales, RUT, direcciones ni antecedentes clínicos.")
    )
}
