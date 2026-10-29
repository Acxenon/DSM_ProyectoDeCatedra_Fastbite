# FastBite

FastBite es una aplicación Android de pedidos de comida que permite explorar un catálogo, buscar productos, administrar un carrito y consultar el historial de pedidos desde una cuenta personal. Está desarrollada con Kotlin, vistas nativas en XML y Firebase.

La interfaz está en español y utiliza precios en dólares. Los formularios de entrega están adaptados a números telefónicos de El Salvador (+503).

## Integrantes

Emmanuel Zenon Lovo Chavarría - LC230428

# Video demostrativo/explicativo

**Enlace:** https://drive.google.com/file/d/1cibg9ynZJtASvT9wvp-tWrmEO7fgkWhS/view?usp=sharing

## Mockups/diseños de pantalla

**Enlace:** https://www.figma.com/design/B1NNJZi9SA5H6fzNiWaTwp/FastBite?node-id=0-1&t=YRQS64Fd5JQ5CbDh-1

## Funcionalidades

- **Cuenta:** registro con nombre, apellido, correo y contraseña; inicio y cierre de sesión; recuperación de contraseña por correo electrónico.
- **Perfil:** consulta del correo y actualización del nombre completo mediante el botón **Actualizar nombre**.
- **Catálogo:** 22 productos entre comidas, entradas, bebidas y postres, con fotografías incluidas en la aplicación.
- **Búsqueda:** por nombre, ingredientes o categoría, sin distinguir mayúsculas ni acentos.
- **Favoritos:** acceso a los productos seleccionados como favoritos.
- **Carrito:** cantidades de 1 a 20 por producto, eliminación de artículos y vaciado con confirmación.
- **Entrega:** dirección, referencia, notas y teléfono de exactamente 8 dígitos, con prefijo +503 fijo.
- **Confirmación:** opción de pago en efectivo al recibir, impuestos incluidos y envío fijo de $2.50. Los importes se calculan en centavos.
- **Pedidos:** historial con búsqueda, filtros, detalles, cancelación de pedidos pendientes y repetición a precios actuales.
- **Recepción:** botón **Marcar como entregado**, con confirmación y traslado del pedido al apartado **Entregados**.
- **Persistencia:** datos locales separados por cuenta y sincronización de pedidos con Cloud Firestore.

## Alcance del proyecto

FastBite es un proyecto académico que implementa el flujo de pedidos del lado del cliente. Permite registrar cuentas, guardar pedidos y consultar su estado, pero no está conectado con restaurantes ni servicios de reparto y no procesa cobros reales.

Los pedidos nuevos aparecen como **Pendiente de confirmación del restaurante**. La acción **Marcar como entregado** registra la confirmación de recepción indicada por el usuario; no verifica una entrega mediante un sistema externo. Los pedidos entregados o cancelados mantienen su estado final.

## Requisitos

- Android Studio y Git para obtener y abrir el código fuente.
- Los SDK y el JDK compatibles con las versiones declaradas en los archivos Gradle del proyecto.
- Un emulador o dispositivo con Android 7.0 (API 24) o superior.
- Conexión a Internet para autenticarse, recuperar contraseñas y sincronizar pedidos.
- Acceso a un proyecto Firebase para configurar una instancia propia de la aplicación.

## Instalación y ejecución

1. Clona el repositorio:

   ```bash
   git clone https://github.com/Acxenon/DSM_ProyectoDeCatedra_Fastbite.git
   ```

2. En Android Studio, selecciona **Open** y abre la carpeta clonada que contiene `settings.gradle.kts`.
3. Espera a que finalice la sincronización de Gradle e instala los componentes que solicite el IDE.
4. Configura Firebase siguiendo la sección siguiente.
5. Selecciona un emulador o dispositivo conectado y ejecuta la aplicación con **Run**.

También es posible descargar el repositorio mediante **Code → Download ZIP**, extraerlo y abrir la carpeta del proyecto. El archivo `local.properties` se genera en cada equipo y no debe compartirse como configuración del proyecto.

## Configuración de Firebase

La aplicación utiliza Firebase Authentication para las cuentas y Cloud Firestore para los pedidos. El identificador del paquete Android es `com.example.fastbite`.

### Configurar una instancia propia

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Registra una aplicación Android con el paquete `com.example.fastbite`.
3. Descarga su archivo `google-services.json` y colócalo en `app/google-services.json`, reemplazando la configuración existente.
4. En **Authentication → Método de acceso**, habilita **Correo electrónico/contraseña**.
5. En **Firestore Database**, crea la base de datos predeterminada `(default)` y selecciona su ubicación.
6. En la pestaña **Reglas**, publica las reglas del archivo [`firestore.rules`](firestore.rules). Si el proyecto Firebase ya tiene otras colecciones, integra las reglas conservando las que necesiten esas colecciones.
7. Sincroniza Gradle y vuelve a ejecutar la aplicación.

Si se utiliza el entorno compartido del equipo, una persona con permisos sobre ese proyecto Firebase debe realizar la configuración de los servicios y las reglas. La configuración incluida corresponde al proyecto `fastbitefb`; no es necesario que cada persona colaboradora cree otro entorno si el equipo utiliza uno común.

### Comprobar la sincronización

Registra una cuenta y confirma un pedido. En **Pedidos**, pulsa **↻** y comprueba que el pedido indique **Sincronizado**. En Firestore debe aparecer en la ruta:

```text
users/{uid}/orders/{orderId}
```

El catálogo está definido en `data/FoodCatalog.kt`, por lo que no es necesario cargarlo manualmente en Firebase.

### Recuperación de contraseña

En el inicio de sesión, **¿Olvidaste tu contraseña?** permite solicitar un enlace de restablecimiento. La aplicación valida el formato del correo y muestra una respuesta genérica para proteger la privacidad de las cuentas. Si el mensaje no llega, revisa la carpeta de correo no deseado.

La aplicación solicita el idioma español a Firebase Authentication. El contenido del correo y del flujo de recuperación también depende de las plantillas configuradas en Firebase.

## Cómo utilizar la aplicación

1. **Crea una cuenta** con nombre, apellido, correo y contraseña, e inicia sesión.
2. **Explora Inicio o Buscar** para consultar los productos y añadirlos al carrito.
3. **Revisa el carrito**, ajusta las cantidades y comprueba el subtotal, el envío y el total.
4. **Completa la entrega** con dirección, referencia y teléfono. Escribe solo los 8 dígitos nacionales: el prefijo +503 ya aparece fuera del campo.
5. **Confirma el pedido**. El carrito se vacía y el pedido queda disponible en el historial.
6. **Consulta Pedidos** para revisar detalles, filtrar, repetir o cancelar un pedido pendiente.
7. **Confirma la recepción** con **Marcar como entregado**. El pedido aparecerá en **Entregados**.

Desde **Perfil** se puede actualizar el nombre completo y administrar la dirección guardada.

## Almacenamiento y conexión

Los pedidos se guardan localmente y se sincronizan con Firestore usando un identificador único. Si una sesión ya está iniciada, un pedido puede quedar guardado en el dispositivo mientras no haya conexión; la aplicación indica que está pendiente de sincronizar. Al recuperar la conexión, pulsa **↻** en Pedidos para reintentar.

Para consultar pedidos desde otro dispositivo, inicia sesión con la misma cuenta y actualiza el historial. La consulta remota obtiene los 100 pedidos más recientes y la copia local conserva los pedidos que ya tiene.

El carrito, los favoritos y las direcciones se almacenan únicamente en el dispositivo y se separan por usuario. La sincronización entre dispositivos corresponde a los pedidos.

## Estructura del proyecto

Los controladores Kotlin se encuentran en `app/src/main/java/com/example/fastbite/` y los recursos Android en `app/src/main/res/`.

| Área | Ubicación | Responsabilidad |
| --- | --- | --- |
| Navegación principal | `MainActivity.kt` | Sesión, navegación y ciclo de vida de las pantallas. |
| Acceso y registro | `LoginActivity.kt`, `RegisterActivity.kt` | Autenticación, registro y recuperación de contraseña. |
| Pantallas | `ui/screens/` | Inicio, búsqueda, carrito, confirmación, pedidos y perfil. |
| Componentes compartidos | `ui/components/`, `ui/UiDialogs.kt` | Tarjetas, formularios y confirmaciones reutilizables. |
| Estado y formato | `ui/AppUiState.kt`, `ui/UiFormat.kt` | Navegación, borradores, importes, fechas y estados. |
| Modelos | `model/` | Productos, líneas de pedido y pedidos. |
| Datos | `data/` | Catálogo, almacenamiento local y acceso a Firestore. |
| Reglas de negocio | `domain/` | Cálculo de importes, validación telefónica, estados y errores de autenticación. |
| Diseños | `res/layout/` | Pantallas, tarjetas y diálogos XML. |
| Estilos y textos | `res/values/` | Colores, estilos y cadenas de la interfaz. |
| Fotografías | `res/drawable-nodpi/` | Imágenes de los productos, disponibles sin descarga. |
| Reglas de acceso | `firestore.rules` | Acceso a los pedidos por usuario y restricciones de actualización. |
| Pruebas unitarias | `app/src/test/java/com/example/fastbite/` | Comprobaciones de la lógica del proyecto. |

Para modificar una pantalla, abre su XML en **Design** o **Split** de Android Studio. Las tarjetas de los listados se definen en los archivos `item_*.xml`. Los productos y sus precios se editan en `data/FoodCatalog.kt`; `ui/FoodImages.kt` relaciona cada producto con su fotografía.

## Pruebas y validación

Las pruebas unitarias se pueden ejecutar desde Android Studio o con Gradle:

**Windows (PowerShell):**

```powershell
.\gradlew.bat testDebugUnitTest
```

**Linux o macOS:**

```bash
./gradlew testDebugUnitTest
```

Para comprobar el flujo de forma manual:

1. Registra una cuenta y verifica que el nombre completo aparezca en Perfil.
2. Añade dos hamburguesas clásicas de $6.50 y una limonada de $2.00. Con esos precios, el subtotal debe ser $15.00 y el total con envío, $17.50.
3. Comprueba que no se permita confirmar con un teléfono de menos de 8 dígitos ni escribir más de 8.
4. Confirma el pedido y verifica sus datos, el vaciado del carrito y la persistencia al reabrir la app.
5. Prueba la búsqueda, los filtros, la repetición, la cancelación y la opción **Marcar como entregado**.
6. Cambia de cuenta y comprueba que no se muestren los datos locales del usuario anterior.
7. Con una sesión iniciada, guarda un pedido sin conexión; después recupera la conexión y actualiza para comprobar que se sincronice sin duplicarse.

La validación previa del código incluyó recursos Android, compilación independiente Java/Kotlin y pruebas de lógica. Estas comprobaciones no sustituyen una compilación completa con Gradle ni las pruebas en un dispositivo con el entorno Firebase configurado.

## Consideraciones para ampliar el proyecto

Las reglas de Firestore aíslan los pedidos por usuario y limitan las actualizaciones de estado realizadas desde el cliente. Para convertir el proyecto en un servicio comercial se necesita un backend que valide precios, inventario, cobros y transiciones de estado, además de la integración con restaurantes y repartidores. Los importes enviados por la aplicación no equivalen a un cobro validado por servidor.

## Documentación de referencia

- [Firebase Authentication para Android](https://firebase.google.com/docs/auth/android/manage-users)
- [Escritura de datos en Cloud Firestore](https://firebase.google.com/docs/firestore/manage-data/add-data)
- [Condiciones de las reglas de seguridad de Firestore](https://firebase.google.com/docs/firestore/security/rules-conditions)
