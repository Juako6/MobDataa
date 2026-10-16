# MobData 🧠📱

Aplicación Android de **autorregistro anónimo de salud mental** desarrollada con **Kotlin + Jetpack Compose** y arquitectura **MVVM**. Permite registrar el estado de ánimo (emoción, intensidad y nota) sin datos personales, guardar cada check-in **en la base de datos local del dispositivo (Room)** y consultar el historial con su detalle.

## Trazabilidad de requerimientos

| Requerimiento | Pantalla | Formulario / Componentes | ViewModel | Modelo / Entidad | Validaciones | Persistencia | Resultado |
|---|---|---|---|---|---|---|---|
| RF01: El usuario ingresa con un alias ficticio | `ui/login/LoginScreen.kt` | `OutlinedTextField` (alias) + `Button` | `LoginViewModel` | `EstadoLogin` | Alias obligatorio · mínimo 3 caracteres | Alias en `SharedPreferences` | Error en `supportingText` o navegación al Home |
| RF02: El usuario registra un check-in de estado de ánimo | `ui/animo/FormularioAnimoScreen.kt` | `FilterChip` (emoción) · `OutlinedButton` (intensidad 1–5) · `OutlinedTextField` (nota) + `Button` Guardar | `AnimoViewModel` | `RegistroAnimo` (@Entity Room) | Emoción obligatoria · intensidad en rango 1–5 | **Room** (`registros_animo`) + notificación de confirmación | Error visible en rojo, banner animado de éxito y notificación local |
| RF03: El usuario consulta el historial y el detalle de sus registros | `ui/historial/HistorialScreen.kt` · `ui/detalle/DetalleRegistroScreen.kt` | `LazyColumn` de `Card` clickeables | `AnimoViewModel` | `RegistroAnimo` | — | Lectura reactiva desde Room | Los registros persistidos aparecen (incluso tras cerrar la app) y navegan a su detalle por id |

## Flujo principal (probado de principio a fin)

```
Usuario ingresa información (emoción + intensidad + nota)
   ↓
Formulario valida (Validaciones: obligatorio + rango)
   ↓         ↘ si falla → mensaje de error visible en rojo
ViewModel procesa (AnimoViewModel)
   ↓
Cambia el estado (StateFlow + banner animado de éxito)
   ↓
La información se guarda (Room: base de datos local)
   ↓
El resultado aparece en pantalla (notificación local + historial reactivo)
   ↓
Historial → Detalle del registro (navegación con paso de id)
```

## Arquitectura MVVM

```
app/src/main/java/com/example/mobdata/
├── MainActivity.kt                  # Solo infla la app
├── model/
│   ├── EstadosUi.kt                 # EstadoFormularioAnimo, EstadoLogin, formatearFecha
│   └── Validaciones.kt              # Reglas puras (testeables sin Android)
├── data/                            # Persistencia local (Room)
│   ├── RegistroAnimo.kt             # @Entity → tabla registros_animo
│   ├── RegistroAnimoDao.kt          # Consultas (Flow reactivo) e inserciones
│   └── AppDatabase.kt               # Base de datos local (singleton)
├── viewmodel/
│   ├── AnimoViewModel.kt            # Estado, validaciones y guardado del check-in
│   └── LoginViewModel.kt            # Estado y validación del alias
├── navigation/
│   └── AppNavigation.kt             # NavController + NavHost (rutas y argumentos)
├── util/
│   └── Notificaciones.kt            # Recurso nativo: notificación de confirmación
└── ui/
    ├── login/LoginScreen.kt
    ├── home/DashboardScreen.kt      # Pestañas Registro / Historial
    ├── animo/FormularioAnimoScreen.kt
    ├── historial/HistorialScreen.kt
    └── detalle/DetalleRegistroScreen.kt
```

**Patrón:** la pantalla muestra información y envía eventos → el ViewModel valida y guarda en Room → Room emite la lista nueva → la pantalla se reconstruye sola. La lógica nunca vive en los componentes visuales.

## Persistencia local (Room)

- Entidad `RegistroAnimo` → tabla `registros_animo` (id autogenerado, emoción, intensidad, nota, fecha como timestamp).
- El DAO expone `obtenerTodos(): Flow<List<RegistroAnimo>>`: **el historial se actualiza automáticamente** cada vez que se inserta un registro.
- `AnimoViewModel` (AndroidViewModel) guarda con `viewModelScope.launch { dao.insertar(...) }`.
- Los registros **sobreviven al cierre de la aplicación** y se recuperan al volver a entrar.

## Recursos nativos utilizados

| Recurso | Dónde | Por qué tiene sentido en esta app |
|---|---|---|
| **Notificaciones locales** (principal) | `util/Notificaciones.kt` + permiso `POST_NOTIFICATIONS` solicitado en runtime | El registro de ánimo es diario: la notificación confirma que el check-in quedó guardado aunque el usuario salga de la app, reforzando la constancia del autorregistro (adherencia). |
| Vibración / feedback háptico | `FormularioAnimoScreen` (`LocalHapticFeedback`) | Confirmación táctil inmediata al seleccionar emoción/intensidad y al guardar. |
| Almacenamiento local | Room + `SharedPreferences` | Los datos son del usuario y deben persistir en su dispositivo (app anónima, sin servidor). |

## Animaciones con propósito

1. **Banner de éxito animado** (`AnimatedVisibility` con fade + expand): aparece solo cuando el registro se guardó — confirmación visual del resultado.
2. **Aparición animada de registros nuevos** (`animateItem` en el historial): al guardar, el registro entra con movimiento al tope de la lista — refleja el cambio de estado causado por el usuario.

## Navegación

Rutas gestionadas con `NavController` / `NavHost`:

- `login` → alias ficticio (persistido en `SharedPreferences`; si existe, la app arranca directo en Home).
- `home` → dashboard con pestañas Registro / Historial.
- `detalle/{registroId}` → el **id viaja como argumento** (`NavType.LongType`); la pantalla consulta el registro en el estado reactivo del ViewModel.

## Pruebas

- **Unitarias (JVM):** `ValidacionesTest` — 7 casos cubriendo las reglas de RF01 y RF02 (vacío, fuera de rango, válidos). Ejecutar con `./gradlew testDebugUnitTest`.
- **Del flujo manual (emulador):** guardar sin emoción → error; guardar válido → banner + notificación + registro en historial; tocar tarjeta → detalle; cerrar y reabrir la app → los registros siguen ahí.

## Cómo compilar

1. Abrir el proyecto en Android Studio (Kotlin 2.2.10, AGP 9, compileSdk 35, minSdk 24).
2. Sincronizar Gradle (descarga KSP y Room automáticamente).
3. `Build → Run 'app'` en emulador o dispositivo físico.

## Equipo y ramas

- Rama `BenjaRama` — Benjamin Leiton
- Rama `Joaquin-Rama` — Joaquin Castro

