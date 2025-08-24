<h2 align="left">Generador de mapeadores para AppWrite Client SDK</h2>

###

<p align="left">**AppwriteMappersGenerator** es una librería de Kotlin que utiliza Kotlin Symbol Processing (KSP) para generar automáticamente código que simplifica el desarrollo con Appwrite y pruebas personalizadas. La librería ofrece la anotacion:</p>

###

<h5 align="left">@AppwriteDocument : Genera funciones para mapear objetos `Document` y `Documents` de Appwrite a clases Kotlin y viceversa.</h5>

###

<p align="left">Esta librería está diseñada para ahorrar tiempo en la conversión manual de datos de Appwrite y facilitar la creación de pruebas, mejorando la productividad en proyectos Kotlin.</p>

###

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" height="40" alt="kotlin logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg" height="40" alt="android logo"  />
</div>

###

<h3 align="left">Tecnologías</h3>

###

<p align="left">- **Lenguaje**: Kotlin<br>- **Procesador de anotaciones**: Kotlin Symbol Processing (KSP)<br>- **Sistema de compilación**: Gradle<br>- **Dependencia principal**: Appwrite Kotlin SDK (versión 5.0.3)<br>- **Plataforma soportada**: JVM (compatible con Android y aplicaciones de servidor Kotlin)</p>

###

<h3 align="left">Instalación</h3>

###

<p align="left">1. **Agrega el repositorio de JitPack** a tu archivo `build.gradle.kts` (o `build.gradle`):</p>

```kotlin
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
```

###

<p align="left">2. **Agrega la dependencia para la librería y KSP**:</p>

```kotlin
    dependencies {
        implementation("com.github.Danielito1996:appwrite-ksp-mapper:1.0.0")
        ksp("com.github.TU_USUARIO:appwrite-ksp-mapper:1.0.0")
    }
```

###

<h3 align="left">Uso</h3>

###

<p align="left">Marca una data class con @AppwriteDocument para generar funciones que mapean objetos Document y Documents de Appwrite a tu clase, y convierten la clase a un Map para Appwrite.</p>

###

<p align="left">Ejemplo:</p>

```kotlin
    @AppwriteDocument
    data class Ofertas(
        val id: String,
        val tittle: String,
        val id_anuncio: String,
        val description: String?,
        val amount: Float?,
        val photoUrl: String?,
        val expired_time: String?
   )
```

###

<p align="left">Código generado:</p>

```kotlin
    com.example.appwrite.models
    fun Document<Map<String, Any>>.toOfertas(): Ofertas {
        return Ofertas(
            id = this.data["id"] as String,
            tittle = this.data["tittle"] as String,
            id_anuncio = this.data["id_anuncio"] as String,
            description = this.data.getOrDefault("description", "") as? String ?: "",
            amount = this.data.getOrDefault("amount", 0f) as? Float ?: 0f,
            photoUrl = this.data.getOrDefault("photoUrl", "") as? String ?: "",
            expired_time = this.data.getOrDefault("expired_time", "") as? String ?: ""
        )
    }
    fun Ofertas.toDocumentData(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "tittle" to tittle,
            "id_anuncio" to id_anuncio,
            "description" to description,
            "amount" to amount,
            "photoUrl" to photoUrl,
            "expired_time" to expired_time
        )
    }
    
    fun Documents<Map<String, Any>>.toOfertasList(): List<Ofertas> {
        return this.documents.map { it.toOfertas() }
    }
```
###

<p align="left">Uso en código:</p>

```kotlin
    fun main() {
        val client = Client()
            .setEndpoint("https://[HOSTNAME]/v1")
            .setProject("YOUR_PROJECT_ID")
            .setKey("YOUR_API_KEY")
    
    val databases = Databases(client)
    
    // Convertir un Document a Ofertas
    val document = databases.getDocument("databaseId", "collectionId", "documentId")
    val oferta = document.toOfertas()
    println(oferta) // Ofertas(id=..., tittle=..., ...)
    
    // Convertir Documents a List<Ofertas>
    val documents = databases.listDocuments("databaseId", "collectionId")
    val ofertas = documents.toOfertasList()
    println(ofertas) // [Ofertas(id=..., tittle=..., ...), ...]
    
    // Convertir Ofertas a Map
    val documentData = oferta.toDocumentData()
    println(documentData) // {id=..., tittle=..., ...}
}

```

###

<h3 align="left">Requisitos</h3>

###

<p align="left">Kotlin 1.9.24 o superior<br>KSP 1.9.24-1.0.24<br>Appwrite Kotlin SDK 5.0.3 o superior (para @AppwriteDocument)<br>Gradle 7.0 o superior</p>

###

<h3 align="left">Contribuir</h3>

###

<p align="left">¡Contribuciones son bienvenidas! Por favor, abre un issue o un pull request en el repositorio de GitHub.</p>

###

<h3 align="left">Licencia</h3>

###

<p align="left">Este proyecto está licenciado bajo la Licencia MIT (LICENSE).</p>

###