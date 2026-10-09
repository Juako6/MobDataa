# Lemac DataLab 🧠📱

Aplicación Android de **autorregistro anónimo de salud mental** desarrollada con **Kotlin + Jetpack Compose** y arquitectura **MVVM**. Permite registrar el estado de ánimo (emoción, intensidad y nota) sin datos personales, y consultar el historial con detalle.

## Trazabilidad de requerimientos (Semana 8)

| Requerimiento | Pantalla | Formulario / Componentes | ViewModel | Modelo (data class) | Validaciones | Resultado |
|---|---|---|---|---|---|---|
| RF01: El usuario ingresa con un alias ficticio | `ui/login/LoginScreen.kt` | `OutlinedTextField` (alias) + `Button` | `LoginViewModel` | `EstadoLogin` | Alias obligatorio · mínimo 3 caracteres | Mensaje de error en `supportingText` o navegación al Home |
| RF02: El usuario registra un check-in de estado de ánimo | `ui/animo/FormularioAnimoScreen.kt` | `FilterChip` (emoción) · `OutlinedButton` (intensidad 1–5) · `OutlinedTextField` (nota) + `Button` Guardar | `AnimoViewModel` | `RegistroAnimo`, `EstadoFormularioAnimo` | Emoción obligatoria · intensidad en rango 1–5 | Error visible en rojo o banner de éxito "Registro guardado" |
| RF03: El usuario consulta el historial y el detalle de sus registros | `ui/historial/HistorialScreen.kt` · `ui/detalle/DetalleRegistroScreen.kt` | `LazyColumn` de `Card` clickeables | `AnimoViewModel` | `RegistroAnimo` | — | El registro guardado aparece en el listado y navega a su detalle con el id |

## Flujo implementado

```
REQUERIMIENTO (RF02)
   ↓
PANTALLA (FormularioAnimoScreen)
   ↓
FORMULARIO (emoción + intensidad + nota)
   ↓
EVENTO -> VIEWMODEL (AnimoViewModel)
   ↓
VALIDACIÓN (obligatorio + rango)  →  error visible si falla
   ↓
MODELO (RegistroAnimo)
   ↓
RESULTADO (banner de éxito + historial actualizado)
   ↓
OTRA PANTALLA (Historial → Detalle por id) 
   ↓
COMMIT
```

## Arquitectura MVVM

```
app/src/main/java/com/example/mobdata/
├── MainActivity.kt                  # Solo infla la app
├── model/
│   └── RegistroAnimo.kt             # Data classes del dominio
├── viewmodel/
│   ├── AnimoViewModel.kt            # Estado, validaciones y guardado del check-in
│   └── LoginViewModel.kt            # Estado y validación del alias
├── navigation/
│   └── AppNavigation.kt             # NavController + NavHost (rutas y argumentos)
└── ui/
    ├── login/LoginScreen.kt
    ├── home/DashboardScreen.kt      # Pestañas Registro / Historial
    ├── animo/FormularioAnimoScreen.kt
    ├── historial/HistorialScreen.kt
    └── detalle/DetalleRegistroScreen.kt
```

**Patrón:** la pantalla muestra información y envía eventos → el ViewModel valida y actualiza el estado (`StateFlow`) → la pantalla se reconstruye con el nuevo estado.

## Navegación

Rutas gestionadas con `NavController` / `NavHost`:

- `login` → pantalla de inicio de sesión con alias ficticio (persistido en `SharedPreferences`).
- `home` → dashboard con pestañas Registro / Historial.
- `detalle/{registroId}` → detalle del registro; el **id viaja como argumento de navegación** (`NavType.LongType`) y el ViewModel lo resuelve con `buscarPorId`.

## Cómo compilar

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. `Build → Run 'app'` en un emulador o dispositivo (minSdk 24, targetSdk 35).

Casos de prueba del formulario:

- **Inválido:** presionar *Guardar* sin emoción → "Debes seleccionar una emoción."
- **Inválido:** emoción sin intensidad → "Selecciona un nivel de intensidad (1 al 5)."
- **Válido:** emoción + intensidad + nota → banner "✅ Registro guardado" y el registro aparece en Historial.

## Equipo y ramas

- Rama `BenjaRama` — Benja
- Rama `Joaquin-Rama` — Joaquín
