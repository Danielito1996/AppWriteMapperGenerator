<h2 align="left">Generador de mapeadores para AppWrite Client SDK</h2>

###

<p align="left">**AppwriteMappersGenerator** es una librería de Kotlin que utiliza Kotlin Symbol Processing (KSP) para generar automáticamente código que simplifica el desarrollo con Appwrite y pruebas personalizadas. La librería ofrece la anotacion:</p>

###

<h5 align="left">@AppwriteDocument : Genera funciones para mapear objetos `Document` y `Documents` de Appwrite a clases Kotlin y viceversa.</h5>

###

<p align="left">Esta librería está diseñada para ahorrar tiempo en la conversión manual de datos de Appwrite y facilitar la creación de pruebas, mejorando la productividad en proyectos Kotlin.</p>

###

<h3 align="left">Tecnologías</h3>

###

<p align="left">- **Lenguaje**: Kotlin<br>- **Procesador de anotaciones**: Kotlin Symbol Processing (KSP)<br>- **Sistema de compilación**: Gradle<br>- **Dependencia principal**: Appwrite Kotlin SDK (versión 5.0.3)<br>- **Plataforma soportada**: JVM (compatible con Android y aplicaciones de servidor Kotlin)</p>

###

<h3 align="left">Instalación</h3>

###

<p align="left">1. **Agrega el repositorio de JitPack** a tu archivo `build.gradle.kts` (o `build.gradle`):<br>   ```kotlin<br>   repositories {<br>           maven { url = uri("https://jitpack.io") }<br>   }</p>

###

<p align="left">2. **Agrega la dependencia para la librería y KSP**:</p>

```kotlin
    dependencies {
        implementation("com.github.Danielito1996:appwrite-ksp-mapper:1.0.0")
        ksp("com.github.TU_USUARIO:appwrite-ksp-mapper:1.0.0")
        }
```

###