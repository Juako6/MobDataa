# MobData 🧠📱

Aplicación Android de **autorregistro anónimo** (estado de ánimo, neurodesarrollo y habilidades DBT) desarrollada con **Kotlin + Jetpack Compose**, arquitectura **MVVM** y persistencia local con **Room (SQLite)**. Funciona sin conexión: todo registro se guarda en el teléfono y sobrevive al cierre de la app. El acceso (RF01) es una **cuenta local** con alias ficticio y contraseña: las credenciales y los datos nunca salen del dispositivo (la contraseña se guarda solo como hash BCrypt).

> ⚠️ **Alcance (RNF04):** prototipo académico. No emite diagnósticos, no recomienda tratamientos y no es un servicio de urgencias. Opera solo con datos ficticios (RNF03).

## Trazabilidad de requerimientos (Entregable 2)

| Requerimiento | Prioridad | Dónde se cumple | Estado |
|---|---|---|---|
| **RF01** Acceso con alias ficticio y contraseña local | Alta | `ui/login/LoginScreen` (modos *Iniciar sesión* / *Crear cuenta*) · `LoginViewModel` · `UsuarioRepository` · entidad `Usuario` + `UsuarioDao` · `Validaciones.validarAlias`/`validarPassword`/`validarConfirmacionPassword` · contraseña hasheada con **BCrypt** (`util/Seguridad.kt`) · sesión recordada 30 días en `util/SesionManager.kt` | ✅ |
| **RF02** Registro de neurodesarrollo (rutina, tareas, episodio sobrecarga/autorregulación, estrategia, resultado) | Alta | `ui/neurodesarrollo/FormularioNeurodesarrolloScreen` · `NeurodesarrolloViewModel` · entidad `RegistroNeurodesarrollo` · `Validaciones.validarNeurodesarrollo` | ✅ |
| **RF03** Diario de habilidades DBT (habilidad del catálogo, emoción/situación, intensidad, utilidad) | Alta | `ui/dbt/FormularioDbtScreen` · `DbtViewModel` · entidad `RegistroDbt` · `Validaciones.validarDbt` · habilidades leídas del catálogo en Room | ✅ |
| **RF04** Editar y eliminar registros propios | Media | `ui/detalle/DetalleRegistroScreen` (botones EDITAR / ELIMINAR con confirmación) · formularios reutilizados en modo edición · `HistorialViewModel.eliminar` | ✅ (los 3 ámbitos) |
| **RF10** Gestión de contenidos y parámetros | Baja | Entidad `ContenidoCatalogo` + `CatalogoDao` (CRUD completo) + `CatalogoSemilla` (17 guías DBT y 4 lecturas) + `ui/recursos/RecursosScreen` (consulta) | 🟡 Catálogo y consulta listos; falta pantalla de administración para el perfil responsable de contenidos |
| **RNF01** Persistencia offline-first | Alta | Room local; cada registro lleva el campo `sincronizado` (visible en historial y detalle como "pendiente de sincronizar") | 🟡 Persistencia ✅ · **la sincronización con la API aún no existe** (no hay API de prueba conectada) |
| **RNF02** Android, tiempos breves, pantallas variables | Alta | Compose adaptable, formularios con scroll, escalas con `weight`, manejo de barras del sistema/teclado | ✅ |
| **RNF03** Minimización de datos | Alta | `Validaciones.contieneDatosPersonales` bloquea RUT, correos y teléfonos en alias y textos libres; avisos "usa solo datos ficticios" en formularios | ✅ |
| **RNF04** Declaración de alcance | Alta | Aviso en login, en Inicio y en la lectura "Alcance de esta aplicación"; sin diagnósticos, sin botón de pánico | ✅ |

### Reglas de negocio del Entregable 1 aplicadas
- **Notificaciones neutrales:** `util/Notificaciones.kt` muestra solo "Registro guardado correctamente." con visibilidad secreta en pantalla de bloqueo (no incluye emoción ni habilidad).
- **Control del dato:** el usuario puede corregir o borrar cualquier registro propio.
- **Arquitectura desacoplada:** la interfaz nunca accede a Room directamente (ver flujo).

## Flujo de datos (Interfaz → ViewModel → Repository → DAO → Room)

```
`Formulario (Compose)`
   ↓ eventos
`ViewModel` ── Validaciones (campos obligatorios, rangos 1–5, largos, datos personales)
   ↓ si falla → mensaje en rojo, NO se guarda
   ↓ viewModelScope.launch { ... }   (corrutinas)
`RegistroRepository` / `UsuarioRepository`
   ↓
`DAO (insertar · obtenerTodos/obtenerPorId · actualizar · eliminar)`
   ↓
`Room → SQLite (lemacdata.db)`
   ↑ Flow reactivo
`Repository → ViewModel (StateFlow) → pantalla se redibuja sola`
```

El **login/registro (RF01)** sigue el mismo flujo: `LoginScreen` → `LoginViewModel` → `Validaciones` (alias, contraseña, confirmación) → `UsuarioRepository` (hash BCrypt con `Seguridad.verificarPassword`) → `UsuarioDao` → tabla `usuarios`. La contraseña en claro nunca se guarda: solo su hash con sal. Al autenticar, `SesionManager` guarda alias + id + expiración (30 días) en `SharedPreferences` para no volver a pedir credenciales en cada arranque.

## CRUD implementado

| Operación | Cómo se hace en la app |
|---|---|
| **Create** | Inicio → elegir módulo → formulario → *Guardar en el teléfono* |
| **Read** | Pestaña *Historial* (todos los ámbitos, ordenado por fecha, filtro por ámbito) y pantalla de detalle |
| **Update** | Detalle → *Editar* → mismo formulario precargado → *Actualizar registro* |
| **Delete** | Detalle → *Eliminar* → confirmación → se borra de Room |

## Estructura del proyecto

```
app/src/main/java/com/example/mobdata/
├── MainActivity.kt
├── data/                              # Persistencia (Room)
├── AppDatabase.kt                 # BD v3 + migraciones 1→2 y 2→3 (conservan datos previos)
│   ├── RegistroAnimo.kt · RegistroNeurodesarrollo.kt · RegistroDbt.kt · ContenidoCatalogo.kt · Usuario.kt   # @Entity
│   ├── RegistroAnimoDao.kt · RegistroNeurodesarrolloDao.kt · RegistroDbtDao.kt · CatalogoDao.kt · UsuarioDao.kt # DAO CRUD
│   ├── RegistroRepository.kt          # Único acceso a datos para los ViewModel
│   ├── UsuarioRepository.kt           # Registro/autenticación de cuentas locales (RF01)
│   └── CatalogoSemilla.kt             # Contenido inicial del catálogo
├── model/                             # Estados de UI, validaciones puras, mapeos
│   ├── Ambito.kt · EstadosUi.kt · ItemHistorial.kt · Validaciones.kt · Credenciales.kt
├── viewmodel/
│   ├── AnimoViewModel · NeurodesarrolloViewModel · DbtViewModel   # crear/editar
│   ├── HistorialViewModel             # listado unificado, filtro, eliminar
│   ├── CatalogoViewModel              # biblioteca de recursos
│   └── LoginViewModel                 # login y registro (RF01)
├── navigation/AppNavigation.kt        # login · home · formulario/{ambito}/{id} · detalle/{ambito}/{id}
├── util/Notificaciones.kt             # Recurso nativo: notificación neutral
├── util/Seguridad.kt                  # Hash BCrypt de contraseñas (RF01)
├── util/SesionManager.kt              # Sesión recordada con expiración (RF01)
└── ui/
    ├── componentes/Componentes.kt     # Selectores, banner de éxito, notificador
    ├── login/ · home/ · animo/ · neurodesarrollo/ · dbt/ · historial/ · detalle/ · recursos/
```

## Recursos nativos
| Recurso | Uso |
|---|---|
| Notificaciones locales (`POST_NOTIFICATIONS` en runtime) | Confirma el guardado de forma neutral |
| Feedback háptico | Al seleccionar opciones y al guardar |
| Almacenamiento local (Room + SharedPreferences) | Persistencia offline de registros, cuentas locales y sesión |

> Siguiendo el Entregable 1, la app **no usa cámara, micrófono ni GPS**.

## Animaciones con propósito
1. **Banner de éxito** (`AnimatedVisibility`: fade + expand) al guardar.
2. **Aparición de registros nuevos** en el historial (`animateItem`).
3. **Despliegue de lecturas/guías** en Recursos (`AnimatedVisibility`).

## Pruebas
- **Unitarias (JVM):** `ValidacionesTest` (alias, contraseña, ánimo, neurodesarrollo, DBT, detección de datos personales), `SeguridadTest` (hash BCrypt: sal distinta por hash, verificación correcta/incorrecta) y `CredencialesTest` (alias duplicado, mensaje único de autenticación, vigencia de sesión). **46 casos en total.** Ejecutar: `./gradlew testDebugUnitTest`.
- **Persistencia real (manual):** guardar → cerrar completamente la app → reabrir → los registros siguen en el historial.
- **Flujo completo a demostrar:** registro de cuenta → cerrar sesión → iniciar sesión con las mismas credenciales → formulario → validación → guardar en Room → listar → editar → eliminar → cerrar app → volver a abrir → datos disponibles.

## Pendientes conocidos
- Sincronización real con la API de prueba (hoy: campo `sincronizado` + indicador en la UI).
- **Recuperación de contraseña:** fuera de alcance por decisión del proyecto. Al ser una cuenta local, una contraseña olvidada deja la cuenta inaccesible (limitación aceptada).
- Módulo **Adicciones** y **recordatorios programados** descritos en el Entregable 1 (no incluidos en esta iteración para no comprometer la estabilidad antes del Parcial 2).
- Pantalla de administración del catálogo para el perfil responsable de contenidos (RF10 parcial).

## Cómo compilar
1. Abrir en Android Studio (Kotlin 2.2.10, AGP 9, compileSdk 35, minSdk 24).
2. Sincronizar Gradle (KSP + Room).
3. *Run 'app'* en emulador o dispositivo.
> Si Room falla por esquema tras cambiar de rama, desinstala la app del emulador una vez.

## Equipo y ramas
- Rama `BenjaRama` — Benjamin Leiton
- Rama `Joaquin-Rama` — Joaquin Castro
