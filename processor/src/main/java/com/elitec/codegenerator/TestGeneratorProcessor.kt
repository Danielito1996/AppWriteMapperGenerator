package com.elitec.codegenerator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class TestGeneratorProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("Iniciando la generacion de codigo")

        // Obtener todas las clases anotadas con @TestGenerator
        val symbols = resolver.getSymbolsWithAnnotation(TestGenerator::class.qualifiedName!!)
        val invalidSymbols = symbols.filter { !it.validate() }.toList()
        logger.info("Obtencion de la anotacion @TestGenerator")

        // Procesar las clases válidas
        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { symbol ->
                val classDeclaration = symbol as KSClassDeclaration
                generateTestFunction(classDeclaration)
            }
        logger.info("Procesamito de clases OK")

        // Devolver los símbolos no válidos para que se procesen en la siguiente ronda
        return invalidSymbols
    }

    private fun generateTestFunction(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()

        // Nombre del archivo generado
        val fileName = "${className}Generated"
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false, classDeclaration.containingFile!!),
            packageName = packageName,
            fileName = fileName
        )

        // Escribir el código generado
        OutputStreamWriter(file).use { writer ->
            writer.write(
                """
                package $packageName
                
                fun ${className}.testGenerated() {
                    println("Funcion de prueba de la clase $className generada")
                }
                """.trimIndent()
            )
        }
    }
}