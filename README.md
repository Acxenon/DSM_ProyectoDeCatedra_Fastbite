# FastBite — aplicación Android de pedidos

Proyecto Kotlin con vistas nativas, Firebase Authentication y sincronización de pedidos en Cloud Firestore. Conserva la configuración de Firebase `fastbitefb` y el paquete `com.example.fastbite`.

# Video Demostrativo: 
ENLACE: 

## Funciones

- Registro con nombre, apellido, correo y contraseña; inicio y cierre de sesión.
- Perfil con nombre completo persistido en Firebase Authentication.
- Inicio con categorías, favoritos y catálogo de 22 comidas, entradas, bebidas y postres.
- Buscar por nombre, ingredientes o categoría, sin distinguir acentos ni mayúsculas.
- Carrito persistente, cantidades entre 1 y 20, eliminación y vaciado con confirmación.
- Dirección, referencia, teléfono y notas para la entrega.
- Confirmación sin campo de cupón ni descuento promocional para pedidos nuevos.
- Confirmación con pago en efectivo al recibir. Precios en dólares e impuestos incluidos; envío fijo de $2.50. Todos los cálculos se realizan en centavos.
- Historial con búsqueda, filtros, detalles, confirmación de recepción con **Marcar como entregado**, cancelación de pendientes y repetición a precios actuales.
- Datos locales separados por UID. Pedido y vaciado del carrito se guardan juntos.
- Pedidos con ID UUID, guardado local y sincronización con `users/{uid}/orders/{orderId}`. Se conserva una copia local y se muestra cuándo falta sincronizar.

## Abrir el proyecto

1. Descomprime el archivo ZIP.
2. En Android Studio selecciona **Open** y abre la carpeta `FastBite-Dsm-master`, donde está `settings.gradle.kts`.
3. Espera la sincronización de Gradle. Conservamos las versiones del proyecto que compartiste: instala los SDK/JDK que Android Studio solicite para esas versiones.
4. Ejecuta en un emulador o teléfono con Android 7.0/API 24 o superior.

El archivo `local.properties` se genera en tu equipo. No se incluyen cachés, APK antiguos ni carpetas `build`.

## Activar Firebase

### Authentication

En el proyecto `fastbitefb`, activa **Authentication → Método de acceso → Correo electrónico/contraseña**. El archivo `app/google-services.json` ya corresponde al proyecto y al paquete `com.example.fastbite`.

### Cloud Firestore (necesario para sincronizar entre dispositivos)

1. En Firebase Console abre tu proyecto `fastbitefb`.
2. En **Firestore Database** crea la base de datos predeterminada `(default)` en modo producción y elige su ubicación.
3. En **Reglas**, copia el contenido completo del archivo `firestore.rules` incluido y pulsa **Publicar**. Estas reglas están diseñadas para este proyecto nuevo; si ya tienes otras colecciones con reglas, intégralas sin perderlas.
4. Registra una cuenta, confirma un pedido y entra a Pedidos. Pulsa **↻** para actualizar.
5. Comprueba que aparezca `users → UID del usuario → orders → ID del pedido` y que la app indique **Sincronizado**.

No hace falta crear manualmente las colecciones ni cargar el catálogo en Firebase: el catálogo está incluido en `data/FoodCatalog.kt`.

Si Firestore no está creado, las reglas deniegan el acceso o no hay conexión, el pedido permanece guardado localmente y se indica **pendiente de sincronizar**. Reintenta con ↻ después de configurar Firestore. Para probar entre dos dispositivos, ambos deben iniciar sesión con la misma cuenta y sincronizar. Las direcciones, favoritos y carrito se guardan solo en el dispositivo. La actualización trae los 100 pedidos más recientes de Firestore; la copia local conserva los pedidos que ya tiene.

## Probar el flujo

1. Registra un usuario con nombre y apellido; inicia sesión y verifica el perfil.
2. Añade 2 hamburguesas clásicas ($6.50 cada una) y 1 limonada ($2.00).
3. Comprueba subtotal $15.00, envío $2.50 y total $17.50.
4. Comprueba que no se solicite código de cupón: total $17.50.
5. Ingresa dirección y teléfono de exactamente 8 dígitos; confirma.
6. Verifica que el carrito se vacíe, el pedido aparezca y sus datos/importe coincidan.
7. Cierra y abre la app: el historial sigue guardado.
8. Prueba buscar, filtrar, repetir y cancelar un pedido pendiente.
9. Cierra sesión y entra con otra cuenta: no debe mostrar el carrito, favoritos o historial de la anterior.
10. Sin conexión, crea un pedido; después conecta y pulsa ↻. Debe sincronizar el mismo ID, sin crear otro pedido.

## Alcance de esta versión

Es una aplicación funcional del lado del cliente para un proyecto académico: registra y guarda pedidos, pero no contacta a restaurantes, no cobra dinero, no asigna repartidores y no simula entregas reales. Un nuevo pedido tiene estado **Pendiente de confirmación del restaurante**.

El usuario puede confirmar que recibió un pedido mediante **Marcar como entregado**. Tras confirmarlo, el pedido pasa a `DELIVERED`, aparece en **Entregados** y se guarda en el dispositivo y Firebase. Los pedidos entregados o cancelados no se pueden volver a abrir. `CONFIRMED` se reserva para un sistema de gestión del restaurante; esta versión permite confirmar la recepción tanto de pedidos pendientes como confirmados.

Las reglas aíslan los pedidos por usuario y restringen las modificaciones del cliente a cancelaciones y confirmaciones de recepción; para un servicio comercial, un backend debe validar precios, cupones, inventario y transiciones de estado. Los importes enviados por esta app no constituyen un cobro validado por servidor.

## Bienvenida y perfil

La pantalla de acceso tiene una marca más visible, bienvenida, fotografía con bordes redondeados y un formulario blanco con campos e iconos alineados. Se conserva la paleta naranja, crema y blanco y el acceso con correo/contraseña. El diseño se edita en `activity_login.xml`; los fondos nuevos están en `bg_login_*.xml` y los textos en `strings.xml`.

El botón del perfil mantiene su estilo y utiliza el texto **Actualizar nombre**, definido en `profile_update_name`. Conserva la actualización del nombre en Firebase.

## Ajustes visuales de esta versión

- Login exclusivamente con correo y contraseña; se retiraron Google, Apple y el separador social.
- Botones Material 3 con altura explícita, texto centrado, tamaño de letra estable y márgenes internos corregidos. Los botones secundarios tienen fondo suave; favoritos y cantidades utilizan controles de 48 dp.
- Cada uno de los 22 productos tiene una fotografía distinta incluida localmente. Se muestra en Inicio, Buscar, carrito y confirmación, sin descargas en ejecución.
- `ui/FoodImages.kt` relaciona el identificador del producto con su fotografía; las imágenes están en `res/drawable-nodpi/food_*.jpg`.
- Los estilos permanecen en `res/values/styles.xml` y el diseño de las tarjetas en `item_food.xml` e `item_cart_line.xml`.
- Fotografías referenciales de Wikimedia Commons: autores, origen y licencias en `CREDITOS_FOTOGRAFIAS.md` y en `app/src/main/assets/photo_credits.txt`. No se modificaron las cuentas ni los datos guardados de Firebase.

## Teléfono de El Salvador

La dirección guardada y la confirmación requieren exactamente 8 dígitos numéricos. El prefijo +503 se muestra fijo fuera del campo; no se escribe dentro. No se pueden guardar números incompletos. Los números anteriores con +503 se reconocen al editar; los datos inválidos se dejan vacíos para corregirlos, sin truncarlos silenciosamente.


## Recuperación de contraseña, idioma y estados de pedido

- **¿Olvidaste tu contraseña?** abre un formulario con el correo escrito en el login. Valida su formato y solicita a Firebase un enlace para restablecer la contraseña.
- Firebase Authentication se configura con idioma `es`: el correo y el flujo alojado de recuperación usan español según la plantilla configurada en Firebase.
- Se muestra una confirmación genérica para no revelar si una dirección tiene una cuenta. Revisa también correo no deseado. No hace falta estar autenticado para recuperar la contraseña.
- Los errores de Firebase se traducen por código en `domain/AuthErrorMessages.kt`; nunca se presenta el mensaje técnico del SDK. El registro utiliza el mismo traductor.
- Se retiraron las tarjetas **Cupones** y **Créditos de fotografías** del perfil. Las atribuciones continúan distribuidas con las imágenes en el proyecto y los assets.
- **Marcar como entregado** solicita confirmación, persiste el estado, muestra la pestaña Entregados y se sincroniza con Firebase. Solo aparece para pedidos pendientes o confirmados. Un pedido entregado/cancelado conserva ese estado final.

### Publicar las reglas actualizadas (necesario)

En tu proyecto `fastbitefb`, abre **Firestore Database → Reglas**, pega el contenido de `firestore.rules` de esta versión y pulsa **Publicar**. La versión anterior de las reglas bloquea el estado `DELIVERED` escrito por el cliente. No basta con sustituir el archivo en Android Studio.

Hasta publicar estas reglas, la app puede guardar el pedido como entregado localmente, pero indicará que está pendiente de sincronizar. Después de publicar, toca **Actualizar historial** en Pedidos para reintentar.


## Organización del proyecto

El diseño está en XML y los eventos/datos se enlazan desde Kotlin. `activity_main.xml` se vuelve a utilizar como contenedor de la aplicación.

| Pantalla | Diseño editable en Android Studio | Controlador |
| --- | --- | --- |
| Inicio | `res/layout/screen_home.xml` | `ui/screens/HomeScreen.kt` |
| Buscar | `res/layout/screen_search.xml` | `ui/screens/SearchScreen.kt` |
| Pedidos | `res/layout/screen_orders.xml` | `ui/screens/OrdersScreen.kt` |
| Perfil | `res/layout/screen_profile.xml` | `ui/screens/ProfileScreen.kt` |
| Carrito | `res/layout/screen_cart.xml` | `ui/screens/CartScreen.kt` |
| Confirmación | `res/layout/screen_checkout.xml` | `ui/screens/CheckoutScreen.kt` |
| Inicio de sesión | `res/layout/activity_login.xml` | `LoginActivity.kt` |
| Registro | `res/layout/activity_register.xml` | `RegisterActivity.kt` |

Todas las rutas Kotlin son relativas a `app/src/main/java/com/example/fastbite/`; los recursos están dentro de `app/src/main/`.

- `MainActivity.kt`: autentica al usuario, enlaza el encabezado/barra inferior, selecciona el controlador y conserva el ciclo de vida. No construye los formularios o tarjetas en código.
- `ui/screens/Screen.kt`: base común para inflar un diseño y recibir cambios del repositorio.
- `ui/AppUiState.kt`: filtros, navegación y borradores; se recuperan al recrear la actividad.
- `ui/UiFormat.kt`: formato de importes, fechas y estados.
- `ui/UiDialogs.kt`: confirmaciones y formulario de dirección de `dialog_address.xml`.
- `ui/ViewEvents.kt`: listener compartido de búsqueda.
- `ui/components/UiComponents.kt`: enlaza las tarjetas y componentes XML con sus datos/eventos.
- `model/`: `Food.kt`, `OrderLine.kt` y `FoodOrder.kt`, sin lógica de pantallas.
- `data/FoodCatalog.kt`: catálogo y búsqueda.
- `data/FastBiteStore.kt`: carrito, favoritos, pedidos y persistencia local por UID.
- `data/FirestoreOrdersDataSource.kt`: lectura y escritura de pedidos en Firebase.
- `domain/OrderPricing.java`: reglas de cálculo en centavos.
- `domain/SalvadorPhone.kt`: validación de los 8 dígitos nacionales y presentación del prefijo +503.
- `domain/OrderTransitions.kt`: transiciones permitidas de pedidos.
- `domain/AuthErrorMessages.kt`: traducción de errores al español.
- `dialog_password_recovery.xml`: formulario de recuperación de contraseña.
- `firestore.rules`: reglas de pedidos privados por usuario.
- `app/src/test/java/com/example/fastbite/`: pruebas de importes, búsqueda y serialización.

### Componentes de diseño reutilizables

- `view_header.xml` y `view_bottom_navigation.xml`: encabezado y navegación, incluidos por `activity_main.xml`.
- `item_food.xml`, `item_cart_line.xml` e `item_order.xml`: tarjetas de comidas, carrito e historial.
- `item_category_chip.xml` e `item_profile_option.xml`: categorías y opciones de perfil.
- `view_summary.xml` y `view_empty.xml`: importes y estados vacíos.
- `dialog_address.xml`: formulario de dirección y teléfono.
- `res/values/styles.xml`, `res/values/colors.xml` y `res/drawable/bg_fb_*.xml`: estilos, colores y fondos compartidos.

Para editar una pantalla, abre su XML en **Design** o **Split** de Android Studio. Los listados se llenan en ejecución con las tarjetas `item_*.xml`; abre también esos archivos para editar cada elemento de la lista. Cambia precios/comidas en `data/FoodCatalog.kt`.

No se han cambiado la identidad del proyecto Firebase, el UID de las cuentas ni las claves de persistencia: se conservan datos y funciones de la versión anterior. Para usar esta versión, descomprime en una carpeta nueva y abre esa carpeta; no superpongas carpetas sobre la anterior, porque podrían quedar clases de los paquetes antiguos.

## Verificación de esta actualización

- 30 pruebas unitarias de importes, búsqueda, serialización, traducción de errores y transiciones de estado: superadas.
- 17 comprobaciones de las reglas contra el emulador local de Firestore: superadas (entrega, cancelación, reintentos, aislamiento por usuario y protección de datos del pedido).
- Código Java/Kotlin y recursos Android: compilados de forma independiente.
- No se realizaron solicitudes de recuperación contra tu cuenta real ni se ejecutó la app en un emulador Android. Prueba el envío y las pantallas en tu Android Studio.

## Verificación realizada

Se compilaron los recursos Android con AAPT2 y se comprobaron XML, IDs y recursos referenciados, compatibilidad del paquete con el JSON de Firebase y los cálculos de importes, búsqueda y serialización de pedidos mediante pruebas unitarias ejecutadas. Se corrigió la extensión del banner original de `.png` a `.jpg` sin cambiar la imagen. Se compiló el código Java/Kotlin de forma independiente contra bibliotecas Android/Firebase; esto no sustituye una compilación completa de Gradle con las versiones exactas del proyecto. En el entorno de preparación no hay Android SDK, por lo que no se realizó una compilación Android ni una prueba de la app en emulador o contra tu cuenta Firebase. La prueba final se realiza siguiendo el flujo anterior en Android Studio.

## Documentación oficial

- https://firebase.google.com/docs/auth/android/manage-users
- https://firebase.google.com/docs/firestore/manage-data/add-data
- https://firebase.google.com/docs/firestore/security/rules-conditions
