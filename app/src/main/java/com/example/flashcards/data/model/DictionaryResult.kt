package com.example.flashcards.data.model

data class DictionaryResult(
    val definition: String,
    val example: String?,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val origin: String?,
    val phonetic: String?,
    val audioUrl: String?
) 