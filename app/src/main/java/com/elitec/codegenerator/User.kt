package com.elitec.codegenerator

@AppwriteDocument
data class User(
    val id: String,
    val tittle: String,
    val id_anuncio: String,
    val description: String?,
    val amount: Float?,
    val photoUrl: String?,
    val expired_time: String?
)