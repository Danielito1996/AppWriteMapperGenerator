package com.elitec.codegenerator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class AppwriteDocumentProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Obtener clases anotadas con @AppwriteDocument
        val symbols = resolver.getSymbolsWithAnnotation(AppwriteDocument::class.qualifiedName!!)
        val invalidSymbols = symbols.filter { !it.validate() }.toList()

        // Procesar clases válidas
        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { symbol ->
                val classDeclaration = symbol as KSClassDeclaration
                generateMapperFunctions(classDeclaration)
            }

        return invalidSymbols
    }

    private fun generateMapperFunctions(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()

        // Validar que sea una data class
        if (!classDeclaration.modifiers.contains(Modifier.DATA)) {
            logger.error("La clase $className debe ser una data class", classDeclaration)
            return
        }

        // Obtener el constructor primario
        val primaryConstructor = classDeclaration.primaryConstructor
        if (primaryConstructor == null) {
            logger.error("La clase $className debe tener un constructor primario", classDeclaration)
            return
        }

        // Obtener propiedades del constructor primario
        val constructorParams = primaryConstructor.parameters.mapNotNull { it.name?.asString() }
        val properties = classDeclaration.getAllProperties()
            .filter { constructorParams.contains(it.simpleName.asString()) }
            .toList()

        // Crear archivo generado
        val fileName = "${className}Mapper"
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false, classDeclaration.containingFile!!),
            packageName = packageName,
            fileName = fileName
        )

        // Generar código
        OutputStreamWriter(file).use { writer ->
            writer.write(
                """
                package $packageName
                
                import io.appwrite.models.Document
                
                fun Document<Map<String, Any>>.to$className(): $className {
                    return $className(
                """.trimIndent()
            )

            // Generar parámetros para to<NombreClase>
            properties.forEachIndexed { index, property ->
                val propertyName = property.simpleName.asString()
                val propertyType = property.type.resolve()
                val typeName = propertyType.toString()
                val isNullable = propertyType.isMarkedNullable

                val accessor = when {
                    typeName == "String?" && !isNullable -> "this.data[\"$propertyName\"] as String"
                    typeName == "String" && !isNullable -> "this.data[\"$propertyName\"] as String"
                    typeName == "String" && isNullable -> "this.data.getOrDefault(\"$propertyName\", \"\") as? String ?: \"\""
                    typeName == "String?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", \"\") as? String ?: \"\""
                    typeName == "Float" && !isNullable -> "this.data[\"$propertyName\"] as Float"
                    typeName == "Float?" && !isNullable -> "this.data[\"$propertyName\"] as Float"
                    typeName == "Float" && isNullable -> "this.data.getOrDefault(\"$propertyName\", 0f) as? Float ?: 0f"
                    typeName == "Float?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", 0f) as? Float ?: 0f"
                    typeName == "Int" && !isNullable -> "this.data[\"$propertyName\"] as Int"
                    typeName == "Int?" && !isNullable -> "this.data[\"$propertyName\"] as Int"
                    typeName == "Int" && isNullable -> "this.data.getOrDefault(\"$propertyName\", 0) as? Int ?: 0"
                    typeName == "Int?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", 0) as? Int ?: 0"
                    typeName == "Boolean" && !isNullable -> "this.data[\"$propertyName\"] as Boolean"
                    typeName == "Boolean?" && !isNullable -> "this.data[\"$propertyName\"] as Boolean"
                    typeName == "Boolean" && isNullable -> "this.data.getOrDefault(\"$propertyName\", false) as? Boolean ?: false"
                    typeName == "Boolean?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", false) as? Boolean ?: false"
                    typeName == "Map<String, Any>" && !isNullable -> "this.data[\"$propertyName\"] as Map<String, Any>"
                    typeName == "Map<String, Any>?" && !isNullable -> "this.data[\"$propertyName\"] as Map<String, Any>"
                    typeName == "Map<String, Any>" && isNullable -> "this.data.getOrDefault(\"$propertyName\", emptyMap()) as? Map<String, Any> ?: emptyMap()"
                    typeName == "Map<String, Any>?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", emptyMap()) as? Map<String, Any> ?: emptyMap()"
                    typeName == "List<String>" && !isNullable -> "this.data[\"$propertyName\"] as List<String>"
                    typeName == "List<String>?" && !isNullable -> "this.data[\"$propertyName\"] as List<String>"
                    typeName == "List<String>" && isNullable -> "this.data.getOrDefault(\"$propertyName\", emptyList()) as? List<String> ?: emptyList()"
                    typeName == "List<String>?" && isNullable -> "this.data.getOrDefault(\"$propertyName\", emptyList()) as? List<String> ?: emptyList()"
                    typeName.startsWith("List<") && typeName.endsWith(">?") -> {
                        val elementType = typeName.removePrefix("List<").removeSuffix(">?")
                        if (isNullable) {
                            "this.data.getOrDefault(\"$propertyName\", emptyList()) as? List<$elementType> ?: emptyList()"
                        } else {
                            "this.data[\"$propertyName\"] as List<$elementType>"
                        }
                    }
                    typeName.startsWith("List<") -> {
                        val elementType = typeName.removePrefix("List<").removeSuffix(">")
                        if (isNullable) {
                            "this.data.getOrDefault(\"$propertyName\", emptyList()) as? List<$elementType> ?: emptyList()"
                        } else {
                            "this.data[\"$propertyName\"] as List<$elementType>"
                        }
                    }
                    else -> {
                        logger.warn("Tipo no soportado para la propiedad $propertyName: $typeName")
                        "null /* Tipo no soportado: $typeName */"
                    }
                }

                writer.write("        $propertyName = $accessor")
                if (index < properties.size - 1) writer.write(",")
                writer.write("\n")
            }

            writer.write(
                """
                    )
                }
                
                fun $className.toDocumentData(): Map<String, Any?> {
                    return mapOf(
                """.trimIndent()
            )

            // Generar parámetros para toDocumentData
            properties.forEachIndexed { index, property ->
                val propertyName = property.simpleName.asString()
                writer.write("        \"$propertyName\" to $propertyName")
                if (index < properties.size - 1) writer.write(",")
                writer.write("\n")
            }

            writer.write(
                """
                    )
                }
                """.trimIndent()
            )
        }
    }
}